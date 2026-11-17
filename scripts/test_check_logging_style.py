import unittest

from check_logging_style import violations


class LoggingStyleTest(unittest.TestCase):
    def test_checks_every_visibility_including_protected(self):
        source = '''class Example {
            private static final Logger privateLog = null;
            protected static final Logger LOG = log;
            public Logger publicLog;
            Logger packageLog;
        }'''
        self.assertEqual(4, len(list(violations(source))))

    def test_alias_without_factory_does_not_evade_the_gate(self):
        self.assertEqual([(1, 'Handwritten Logger declaration: LOG')],
                         list(violations('protected static final Logger LOG = log;')))

    def test_ignores_comments_strings_and_imports(self):
        source = '''import org.slf4j.Logger;
        // protected static final Logger LOG = log;
        class Example {
            String text = "LoggerFactory.getLogger(Example.class)";
            String block = """ Logger LOG = log; """;
        }'''
        self.assertEqual([], list(violations(source)))

    def test_checks_multiline_fully_qualified_factory_calls(self):
        source = 'org.slf4j.LoggerFactory\n . getLogger(Example.class);'
        self.assertEqual(1, len(list(violations(source))))

    def test_accepts_lombok_logger_usage(self):
        self.assertEqual([], list(violations('@Slf4j class Example { void run() { log.info("ok"); } }')))


if __name__ == '__main__':
    unittest.main()
