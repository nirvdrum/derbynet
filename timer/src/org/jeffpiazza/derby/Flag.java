package org.jeffpiazza.derby;

import java.util.ArrayList;
import java.util.List;

public abstract class Flag<T> {
  private static List<Flag> all_flags = new ArrayList<Flag>();

  public static final Flag<Boolean> version
      = BooleanFlag.readonly("v", "Show version");

  public static final Flag<Boolean> headless
      = BooleanFlag.readonly("x", "Run headless, without GUI.");

  public static final Flag<Boolean> trace_messages
      = BooleanFlag.settable("t", "Trace non-heartbeat messages sent");
  public static final Flag<Boolean> trace_heartbeats
      = BooleanFlag.settable("th", "Trace heartbeat messages sent");
  public static final Flag<Boolean> trace_responses
      = BooleanFlag.settable("r", "Trace responses to traced messages");

  public static final Flag<String> logdir
      = StringFlag.readonly("logdir", null, "Write log files in <directory>");

  public static final Flag<Boolean> mark_ignored_timer_responses
      = BooleanFlag.settable("mark_ignored_timer_responses",
                             "Mark in the log timer responses that were ignored");

  public static final Flag<Boolean> insecure
      = BooleanFlag.settable("insecure",
                             "Ignore any HTTPS certificate problems.  (Use with"
                             + " caution!)");

  public static final Flag<String> username
      = StringFlag.readonly("u", "Timer",
                            "Specify username for authenticating to web server");
  public static final Flag<String> password
      = StringFlag.readonly("p", "",
                            "Specify password for authenticating to web server");
  public static final Flag<String> portname
      = StringFlag.readonly("n", null,
                            "Use specified port name instead of searching");
  public static final Flag<String> devicename
      = StringFlag.readonly("d", null,
                            "Use specified device instead of trying to identify");

  public static final Flag<Boolean> ignore_place
      = BooleanFlag.settable("ignore-place",
                             "Discard any place indications from timer");

  public static final Flag<Boolean> legacy_implementations
      = BooleanFlag.settable("legacy",
                             "Use legacy (non-profile-based) implementations");

  public static final Flag<Long> delay_reset_after_race
      = LongFlag.settable("delay-reset-after-race", 10,
                          "How long after race over before timer will be reset,"
                          + " default 10s.  (For SmartLine, DerbyMagic, NewBold,"
                          + " and BertDrake.)");

  public static final Flag<Long> newline_expected_ms
      = LongFlag.settable("newline-expected-ms", 200,
                          "After this many milliseconds, assume an unterminated"
                          + " line from the timer is complete"
                          + " (0 = wait forever).");

  public static final Flag<Boolean> clear_rts_dtr
      = BooleanFlag.settable("clear-rts-dtr",
                             "EXPERIMENTAL Initially clear RTS and DTR lines on"
                             + " serial port by default.");

  public static final Flag<Boolean> log_matchers
      = BooleanFlag.settable("log-matchers",
                             "Write matcher results to log (for debugging)");

  // Issue #35: Reject gate state changes that don't last "reasonably" long.
  // To do that, don't record a gate state change until it's aged a bit.
  //
  public static final Flag<Long> min_gate_time
      = LongFlag.settable("min-gate-time", 500,
                          "Ignore gate transitions shorter than <milliseconds>");

  public static final Flag<Boolean> simulate_timer
      = BooleanFlag.readonly("simulate-timer",
                             "Simulate timer device (for testing)");
  public static final Flag<Boolean> simulate_host
      = BooleanFlag.readonly("simulate-host",
                             "Exercise timer with simulated host");
  public static final Flag<Integer> lanes
      = IntegerFlag.readonly("lanes", 0, "Specify number of lanes to report");
  public static final Flag<Integer> pace
      = IntegerFlag.settable("pace", 0,
                             "Simulation staging pace (seconds between heats)");
  public static final Flag<Boolean> simulate_has_not_spoken
      = BooleanFlag.readonly("simulate-has-not-spoken",
                             "Simulate simulated timer has never spoken");

  public static final Flag<Boolean> no_gate_watcher
      = BooleanFlag.settable("no-gate-watcher",
                             "Disable interrogation of timer's gate state.");

  public static final Flag<String> obs_uri
      = StringFlag.settable("obs-uri", null,
                            "URI, e.g. ws://derbynet.local:4444, for OBS "
                            + "websocket, to which to send hotkey events for "
                            + "heat start and heat finish");
  public static final Flag<String> obs_password
      = StringFlag.settable("obs-password", "",
                            "Password for OBS websocket, if any.");
  public static final Flag<String> obs_start
      = StringFlag.settable("obs-start", null,
                            "Hotkey name to be sent to OBS websocket when a heat "
                            + "starts.  If first character is '@', instead names "
                            + "a file containing an aribtrary OBS websocket request.");
  public static final Flag<String> obs_finish
      = StringFlag.settable("obs-finish", null,
                            "Hotkey name to be sent to OBS websocket when a heat "
                            + "ends.  If first character is '@', instead names "
                            + "a file containing an aribtrary OBS websocket request.");
  public static final Flag<Boolean> record
      = BooleanFlag.readonly("record", null);
  public static final Flag<String> playback
      = StringFlag.readonly("playback", null, null);

  public static final Flag<Integer> reset_after_start
      = IntegerFlag.settable("reset-after-start", 10,
                             "TheJudge: Reset timer <nsec> seconds after heat "
                             + "start, default 10");

  public static final Flag<Boolean> skip_enhanced_format
      = BooleanFlag.settable("skip-enhanced-format",
                             "FastTrack: Don't attempt enhanced format command");
  public static final Flag<Boolean> skip_read_features
      = BooleanFlag.settable("skip-read-features",
                             "FastTrack: Don't attempt reading features");
  public static final Flag<Boolean> fasttrack_automatic_gate_release
      = BooleanFlag.settable("fasttrack-automatic-gate-release",
                             "FastTrack light tree and automatic gate release installed");

  public static final Flag<Boolean> debug_io
      = BooleanFlag.settable("debug-io",
                             "Enable debugging for low-level serial communication");

  public Flag(String name, T value, String description) {
    this.name = name;
    this.value = value;
    this.description = description;
    all_flags.add(this);
  }

  public Flag(String name, T value, String description, boolean settable) {
    this(name, value, description);
    this.is_settable = settable;
  }

  public static int parseCommandLineFlags(String[] args, int argc) {
    boolean advanced;
    do {
      advanced = false;
      for (int i = 0; i < all_flags.size(); ++i) {
        int new_argc = all_flags.get(i).maybeParseCommandLine(args, argc);
        if (new_argc != argc) {
          argc = new_argc;
          advanced = true;
        }
      }
    } while (advanced);
    return argc;
  }

  public static void usage() {
    for (int i = 0; i < all_flags.size(); ++i) {
      System.err.println(all_flags.get(i).usage_string());
    }
  }

  public static Flag[] allFlags() {
    return all_flags.toArray(new Flag[all_flags.size()]);
  }

  public static void assignFlag(String flag, String value) {
    for (Flag f : all_flags) {
      if (f.name().equals(flag)) {
        f.setValueText(value);
        return;
      }
    }
  }

  public abstract int maybeParseCommandLine(String[] args, int argc);

  public abstract String typeName();

  public abstract void setValueText(String valueText);

  public String name() {
    return name;
  }

  public T value() {
    return value;
  }

  public String description() {
    return description;
  }

  public boolean is_settable() {
    return is_settable;
  }

  public String usage_string() {
    return "   -" + name() + ": " + description;
  }

  protected final String name;
  protected T value;
  private String description;
  protected boolean is_settable;

  public static class BooleanFlag extends Flag<Boolean> {
    public static BooleanFlag settable(String name, String description) {
      return new BooleanFlag(name, description, true);
    }

    public static BooleanFlag readonly(String name, String description) {
      return new BooleanFlag(name, description, false);
    }

    private BooleanFlag(String name, String description, boolean settable) {
      super(name, Boolean.FALSE, description, settable);
    }

    public String typeName() {
      return "bool";
    }

    public void setValueText(String valueText) {
      value = !valueText.equals("false");
    }

    @Override
    public int maybeParseCommandLine(String[] args, int argc) {
      if (argc >= args.length) {
        return argc;
      }
      if (args[argc].equals("-" + name)) {
        value = Boolean.TRUE;
        return argc + 1;
      }
      if (args[argc].equals("-no-" + name)) {
        value = Boolean.FALSE;
        return argc + 1;
      }
      return argc;
    }
  }

  public static class StringFlag extends Flag<String> {
    public static StringFlag readonly(String name, String value,
                                      String description) {
      return new StringFlag(name, value, description, false);
    }

    public static StringFlag settable(String name, String value,
                                      String description) {
      return new StringFlag(name, value, description, true);
    }

    private StringFlag(String name, String value,
                       String description, boolean settable) {
      super(name, value, description, settable);
    }

    public String typeName() {
      return "string";
    }

    public void setValueText(String valueText) {
      value = valueText;
    }

    @Override
    public int maybeParseCommandLine(String[] args, int argc) {
      if (argc + 1 >= args.length) {
        return argc;
      }
      if (args[argc].equals("-" + name)) {
        value = args[argc + 1];
        return argc + 2;
      }
      return argc;
    }
  }

  public static class LongFlag extends Flag<Long> {
    public static LongFlag readonly(String name, Long value, String description) {
      return new LongFlag(name, value, description, false);
    }

    public static LongFlag settable(String name, Long value, String description) {
      return new LongFlag(name, value, description, true);
    }

    public static LongFlag readonly(String name, long value, String description) {
      return new LongFlag(name, value, description, false);
    }

    public static LongFlag settable(String name, long value, String description) {
      return new LongFlag(name, value, description, true);
    }

    private LongFlag(String name, Long value,
                     String description, boolean settable) {
      super(name, value, description, settable);
    }

    public String typeName() {
      return "long";
    }

    public void setValueText(String valueText) {
      try {
        value = Long.parseLong(valueText);
      } catch (NumberFormatException nfe) {
        LogWriter.info(name() + " value failed to parse: " + valueText);
        System.err.println(name() + " value failed to parse: " + valueText);
      }
    }

    @Override
    public int maybeParseCommandLine(String[] args, int argc) {
      if (argc + 1 >= args.length) {
        return argc;
      }
      if (args[argc].equals("-" + name)) {
        setValueText(args[argc + 1]);
        return argc + 2;
      }
      return argc;
    }
  }

  public static class IntegerFlag extends Flag<Integer> {
    public static IntegerFlag readonly(String name, Integer value,
                                       String description) {
      return new IntegerFlag(name, value, description, false);
    }

    public static IntegerFlag settable(String name, Integer value,
                                       String description) {
      return new IntegerFlag(name, value, description, true);
    }

    private IntegerFlag(String name, Integer value,
                        String description, boolean settable) {
      super(name, value, description, settable);
    }

    public String typeName() {
      return "int";
    }

    public void setValueText(String valueText) {
      try {
        value = Integer.parseInt(valueText);
      } catch (NumberFormatException nfe) {
        LogWriter.info(name() + " value failed to parse: " + valueText);
        System.err.println(name() + " value failed to parse: " + valueText);
      }
    }

    @Override
    public int maybeParseCommandLine(String[] args, int argc) {
      if (argc + 1 >= args.length) {
        return argc;
      }
      if (args[argc].equals("-" + name)) {
        setValueText(args[argc + 1]);
        return argc + 2;
      }
      return argc;
    }
  }
}
