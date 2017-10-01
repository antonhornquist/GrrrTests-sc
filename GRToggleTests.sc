GRToggleTests : Test {
	var
		smallHorizontalToggle4x1,
		largeHorizontalToggle8x2,
		gotLed
	;

	setup {
		GRTestsHelper.saveGlobals;
		GRTestsHelper.disableTraceAndFlash;
		smallHorizontalToggle4x1 = GRHToggle.newDetached(4, 1);
		largeHorizontalToggle8x2 = GRHToggle.newDetached(8, 2);
		gotLed = Array.new;
	}

	teardown {
		GRTestsHelper.restoreGlobals;
	}

	addTestLedEventListener { |view|
		var listener;
		listener = { |source, point, on|
			gotLed = gotLed.add( ( source: source.id, point: point, on: on ) );
		};
		view.addLedEventListener(listener);
		^listener
	}

	// Tests
	test_defaults {
		var view = smallHorizontalToggle4x1;
		this.assertEqual(0, view.value);
		this.assert(view.isCoupled);
	}

	test_its_possible_to_create_a_decoupled_toggle_from_scratch {
		this.assertEqual(false, GRHToggle.newDecoupled(nil, nil, 8, 2).isCoupled);
	}

	test_its_possible_to_change_value {
		var
			view = largeHorizontalToggle8x2
		;

		view.value = 2;
		this.assertEqual(2, view.value);

		view.value = 4;
		this.assertEqual(4, view.value);

		view.value = nil;
		this.assertEqual(nil, view.value);

		this.assertErrorThrown(Error) { view.value = 8 };
		this.assertErrorThrown(Error) { view.value = -1 };
	}

	test_a_value_change_sets_new_value_and_refreshes_leds {
		var toggle, gotAction, gotPressAction;
		toggle = GRHToggle.newDetached(4, 1);
		toggle.id = \toggle;

		this.addTestLedEventListener(toggle);

		gotAction = nil;
		toggle.action = { |toggle, value|
			gotAction = ( toggle: toggle, value: value )
		};

		gotPressAction = nil;
		toggle.togglePressedAction = { |toggle, value, pressed|
			gotPressAction = ( toggle: toggle, value: value, pressed: pressed )
		};

		toggle.value = 3;

		this.assertEqual(3, toggle.value);
		this.assertEqual(
			[
				( source: \toggle, point: Point.new(0, 0), on: false ),
				( source: \toggle, point: Point.new(1, 0), on: false ),
				( source: \toggle, point: Point.new(2, 0), on: false ),
				( source: \toggle, point: Point.new(3, 0), on: true )
			],
			gotLed
		);
		this.assertEqual(
			nil,
			gotAction
		);
		this.assertEqual(
			nil,
			gotPressAction
		);

		gotLed = Array.new; // TODO: or reset_test_led_event_listener_x

		gotAction = nil;
		gotPressAction = nil;

		toggle.value = nil;
		this.assertEqual(
			[
				( source: \toggle, point: Point.new(0, 0), on: false ),
				( source: \toggle, point: Point.new(1, 0), on: false ),
				( source: \toggle, point: Point.new(2, 0), on: false ),
				( source: \toggle, point: Point.new(3, 0), on: false )
			],
			gotLed
		);
		this.assertEqual(
			nil,
			gotAction
		);
		this.assertEqual(
			nil,
			gotPressAction
		);
	}

	test_a_value_action_change_sets_new_value_refreshes_leds_and_trigger_actions {
		var toggle, gotAction, gotPressAction;

		toggle = GRHToggle.newDetached(4, 1);
		toggle.id = \toggle;

		this.addTestLedEventListener(toggle);

		gotAction = nil;
		toggle.action = { |toggle, value|
			gotAction = ( toggle: toggle, value: value )
		};

		gotPressAction = nil;
		toggle.togglePressedAction = { |toggle, value, pressed|
			gotPressAction = ( toggle: toggle, value: value, pressed: pressed )
		};

		toggle.valueAction = 3;

		this.assertEqual(3, toggle.value);
		this.assertEqual(
			[
				( source: \toggle, point: Point.new(0, 0), on: false ),
				( source: \toggle, point: Point.new(1, 0), on: false ),
				( source: \toggle, point: Point.new(2, 0), on: false ),
				( source: \toggle, point: Point.new(3, 0), on: true )
			],
			gotLed
		);
		this.assertEqual(
			( toggle: toggle, value: 3 ),
			gotAction
		);
		this.assertEqual(
			nil,
			gotPressAction
		);

		gotLed = Array.new; // TODO: or reset_test_led_event_listener_x

		gotAction = nil;
		gotPressAction = nil;

		toggle.valueAction = nil;
		this.assertEqual(
			[
				( source: \toggle, point: Point.new(0, 0), on: false ),
				( source: \toggle, point: Point.new(1, 0), on: false ),
				( source: \toggle, point: Point.new(2, 0), on: false ),
				( source: \toggle, point: Point.new(3, 0), on: false )
			],
			gotLed
		);
		this.assertEqual(
			( toggle: toggle, value: nil ),
			gotAction
		);
		this.assertEqual(
			nil,
			gotPressAction
		);
	}

	test_coupled_toggle {
		var toggle, gotAction, gotPressAction;

		toggle = GRHToggle.newDetached(8, 2);
		this.assertEqual(0, toggle.value);

		gotAction = nil;
		toggle.action = { |toggle, value|
			gotAction = ( toggle: toggle, value: value )
		};

		gotPressAction = nil;
		toggle.togglePressedAction = { |toggle, value, pressed|
			gotPressAction = ( toggle: toggle, value: value, pressed: pressed )
		};

		toggle.press(Point.new(4, 1));

		this.assertEqual(4, toggle.value);
		this.assertEqual(
			( toggle: toggle, value: 4 ),
			gotAction
		);
		this.assertEqual(
			( toggle: toggle, value: 4, pressed: true ),
			gotPressAction
		);

		gotAction = nil;
		gotPressAction = nil;

		toggle.release(Point.new(4, 1));

		this.assertEqual(4, toggle.value);
		this.assertEqual(
			nil,
			gotAction
		);
		this.assertEqual(
			( toggle: toggle, value: 4, pressed: false ),
			gotPressAction
		);

	}

	test_decoupled_toggle {
		var toggle, gotAction, gotPressAction;

		toggle = GRHToggle.newDecoupled(nil, nil, 8, 2);
		this.assertEqual(0, toggle.value);

		gotAction = nil;
		toggle.action = { |toggle, value|
			gotAction = ( toggle: toggle, value: value );
		};

		gotPressAction = nil;
		toggle.togglePressedAction = { |toggle, value, pressed|
			gotPressAction = ( toggle: toggle, value: value, pressed: pressed );
		};

		toggle.press(Point.new(4, 1));

		this.assertEqual(0, toggle.value);
		this.assertEqual(
			nil,
			gotAction
		);
		this.assertEqual(
			( toggle: toggle, value: 4, pressed: true ),
			gotPressAction
		);

		gotAction = nil;
		gotPressAction = nil;

		toggle.release(Point.new(4, 1));

		this.assertEqual(0, toggle.value);
		this.assertEqual(
			nil,
			gotAction
		);
		this.assertEqual(
			( toggle: toggle, value: 4, pressed: false ),
			gotPressAction
		);

	}

	test_nil_toggle_option {
		var toggle;

		toggle = GRHToggle.newDetached(8, 2);
		toggle.toggleNil = true;
		this.assertEqual(0, toggle.value);

		toggle.press(Point.new(0, 1));
		toggle.release(Point.new(0, 1));

		this.assertEqual(nil, toggle.value);

		toggle.press(Point.new(0, 1));
		toggle.release(Point.new(0, 1));

		this.assertEqual(0, toggle.value);

		toggle.press(Point.new(2, 1));
		toggle.release(Point.new(2, 1));

		this.assertEqual(2, toggle.value);

		toggle.press(Point.new(2, 1));
		toggle.release(Point.new(2, 1));

		this.assertEqual(nil, toggle.value);

		toggle.press(Point.new(3, 1));
		toggle.release(Point.new(3, 1));

		this.assertEqual(3, toggle.value);
	}
}
