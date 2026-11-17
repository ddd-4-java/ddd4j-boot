#!/usr/bin/env python3
"""Check Java logger declarations across all visibilities and optional Git refs."""
import argparse
import io
import subprocess
import tarfile
from pathlib import Path

from check_no_records import tokens


def violations(source):
    stream = list(tokens(source))
    for index, (value, line) in enumerate(stream):
        following = [item[0] for item in stream[index + 1:index + 4]]
        if value == 'LoggerFactory' and following[:2] == ['.', 'getLogger']:
            yield line, 'Use @Slf4j instead of LoggerFactory.getLogger'
        if value == 'Logger' and len(following) >= 2:
            name = following[0]
            if (name[0].isalpha() or name[0] in '_$') and following[1] in ('=', ';', ','):
                yield line, 'Handwritten Logger declaration: ' + name


def sources(ref=None):
    if ref:
        payload = subprocess.check_output(['git', 'archive', ref])
        with tarfile.open(fileobj=io.BytesIO(payload)) as archive:
            for entry in archive:
                if entry.isfile() and entry.name.endswith('.java'):
                    yield ref + ':' + entry.name, archive.extractfile(entry).read().decode('utf-8')
    else:
        paths = subprocess.check_output(['git', 'ls-files', '-z', '--', '*.java']).split(b'\0')
        for name in paths:
            if name:
                path = Path(name.decode())
                yield str(path), path.read_text(encoding='utf-8')


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    group = parser.add_mutually_exclusive_group()
    group.add_argument('--ref')
    group.add_argument('--all-remotes', action='store_true', help='Check cached GitHub refs, excluding archives')
    args = parser.parse_args()
    refs = [args.ref]
    if args.all_remotes:
        refs = subprocess.check_output([
            'git', 'for-each-ref', '--format=%(refname)', 'refs/remotes/github/'
        ], text=True).splitlines()
        refs = [ref for ref in refs if not ref.endswith('/HEAD') and '/github/archive/' not in ref]
        if not refs:
            parser.error('No cached GitHub branches available')
    errors = 0
    for ref in refs:
        for name, source in sources(ref):
            for line, message in violations(source):
                print(f'{name}:{line}: {message}')
                errors += 1
    print(f'FAIL: {errors} logger violations' if errors else f'PASS: {len(refs)} source snapshot(s), no handwritten loggers')
    return 1 if errors else 0


if __name__ == '__main__':
    raise SystemExit(main())
