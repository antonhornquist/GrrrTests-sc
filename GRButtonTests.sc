GRButtonTests : Test {
	var
		smallToggleButton,
		largeToggleButton,
		smallMomentaryButton,
		largeMomentaryButton
	;

	setup {
		GRTestsHelper.saveGlobals;
		GRTestsHelper.disableTraceAndFlash;
		smallToggleButton = GRButton.newDetached(1, 1);
		smallToggleButton.id = \smallToggleButton;
		largeToggleButton = GRButton.newDetached(2, 2);
		largeToggleButton.id = \largeToggleButton;
		smallMomentaryButton = GRButton.newMomentary(1, 1);
		smallMomentaryButton.id = \smallMomentaryButton;
		largeMomentaryButton = GRButton.newMomentary(2, 2);
		largeMomentaryButton.id = \largeMomentaryButton;
	}

	teardown {
		GRTestsHelper.restoreGlobals;
	}

	// initialization
	test_a_button_should_by_default_be_a_released_coupled_toggle_button_with_value_false {
		button = Button.new_detached
		assert(button.is_released?)
		assert(button.is_coupled?)
		assert_equal(:toggle, button.behavior)
		assert_equal(false, button.value)
	end

	test_it_should_be_possible_to_set_a_buttons_behavior_to_momentary {
		button = Button.new_detached
		button.behavior = :momentary
		assert_equal(:momentary, button.behavior)
	end

	test_it_should_be_possible_to_create_a_momentary_button_from_scratch_{
		button = Button.new_momentary(nil, nil, 1, 1)
		assert_equal(:momentary, button.behavior)
	end

	test_it_should_be_possible_to_decouple_a_button {
		button = Button.new_detached
		button.coupled = false
		assert_equal(false, button.is_coupled?)
	end

	test_it_should_be_possible_to_create_a_decoupled_button_from_scratch {
		button = Button.new_decoupled(nil, nil, 1, 1)
		assert_equal(false, button.is_coupled?)
	end

	// button pressed state and button events
	test_a_single_view_button_press_event_should_make_a_button_pressed {
		button = @small_toggle_button
		button.press(Point.new(0, 0))
		assert(button.is_pressed?)
	end

	test_a_button_should_not_be_considered_released_until_all_view_buttons_are_released {
		button = @large_toggle_button

		button.press(Point.new(0, 0))

		assert(button.is_pressed?)

		button.press(Point.new(1, 0))

		assert(button.is_pressed?)

		button.release(Point.new(0, 0))

		assert(button.is_pressed?)

		button.release(Point.new(1, 0))

		assert(button.is_released?)
	end

	test_when_pressed_state_of_a_button_is_updated_button_pressed_and_released_actions_should_be_triggered {
		button = Button.new_decoupled(nil, nil, 2, 2)
		pressed_listener = MockButtonPressedListener.new(button)
		released_listener = MockButtonReleasedListener.new(button)

		button.press(Point.new(0, 0))
		assert( pressed_listener.has_been_notified_of?( [ [button] ] ) )

		button.press(Point.new(1, 0))
		assert( pressed_listener.has_been_notified_of?( [ [button] ] ) )

		button.release(Point.new(0, 0))
		assert( released_listener.has_not_been_notified_of_anything? )

		button.release(Point.new(1, 0))
		assert( released_listener.has_been_notified_of?( [ [button] ] ) )
	end

	// led events and refresh
	test_when_the_value_of_a_button_is_set_to_true_leds_are_lit {
		button = @large_toggle_button
		view_led_refreshed_listener = MockViewLedRefreshedListener.new(button)

		button.value = true

		assert(
			view_led_refreshed_listener.has_been_notified_of?(
				[
					{ :source => :large_toggle_button, :point => Point.new(0, 0), :on => true },
					{ :source => :large_toggle_button, :point => Point.new(1, 0), :on => true },
					{ :source => :large_toggle_button, :point => Point.new(0, 1), :on => true },
					{ :source => :large_toggle_button, :point => Point.new(1, 1), :on => true },
				]
			)
		)
	end

	test_when_the_value_of_a_button_is_set_to_false_leds_are_unlit {
		button = @large_toggle_button
		button.value = true
		view_led_refreshed_listener = MockViewLedRefreshedListener.new(button)

		button.value = false

		assert(
			view_led_refreshed_listener.has_been_notified_of?(
				[
					{ :source => :large_toggle_button, :point => Point.new(0, 0), :on => false },
					{ :source => :large_toggle_button, :point => Point.new(1, 0), :on => false },
					{ :source => :large_toggle_button, :point => Point.new(0, 1), :on => false },
					{ :source => :large_toggle_button, :point => Point.new(1, 1), :on => false },
				]
			)
		)
	end

	// decoupled button behavior
	test_a_decoupled_button_should_not_toggle_value_nor_trigger_main_action_when_it_is_pressed_and_when_it_is_released {
		button = Button.new_decoupled(nil, nil, 2, 2)
		action_listener = MockActionListener.new(button)

		assert_equal(false, button.value)
		button.press(Point.new(0, 0))
		assert_equal(false, button.value)
		button.release(Point.new(0, 0))
		assert_equal(false, button.value)

		assert( action_listener.has_not_been_notified_of_anything? )
	end

	// coupled toggle button behavior
	test_a_coupled_toggle_button_should_toggle_value_every_time_the_button_is_pressed {
		button = @small_toggle_button

		button.press(Point.new(0, 0))
		assert_equal(true, button.value)
		button.release(Point.new(0, 0))
		assert_equal(true, button.value)
		button.press(Point.new(0, 0))
		assert_equal(false, button.value)
		button.release(Point.new(0, 0))
		assert_equal(false, button.value)
	end

	test_a_coupled_toggle_button_should_trigger_the_main_action_every_time_a_button_is_pressed {
		button = @small_toggle_button
		action_listener = MockActionListener.new(button)

		button.press(Point.new(0, 0))

		assert(
			action_listener.has_been_notified_of?(
				[
					[button, true]
				]
			)
		)

		button.release(Point.new(0, 0))

		assert(
			action_listener.has_been_notified_of?(
				[
					[button, true]
				]
			)
		)

		button.press(Point.new(0, 0))

		assert(
			action_listener.has_been_notified_of?(
				[
					[button, true],
					[button, false]
				]
			)
		)

		button.release(Point.new(0, 0))

		assert(
			action_listener.has_been_notified_of?(
				[
					[button, true],
					[button, false]
				]
			)
		)
	end

	// coupled momentary button behavior
	test_a_coupled_momentary_button_should_toggle_value_both_when_button_is_pressed_and_when_it_is_released {
		button = @small_momentary_button

		button.press(Point.new(0, 0))
		assert_equal(true, button.value)
		button.release(Point.new(0, 0))
		assert_equal(false, button.value)

		button.value=true

		button.press(Point.new(0, 0))
		assert_equal(false, button.value)
		button.release(Point.new(0, 0))
		assert_equal(true, button.value)
	end

	test_a_coupled_momentary_button_should_trigger_main_action_both_when_button_is_pressed_and_when_it_is_released {
		button = @small_momentary_button
		action_listener = MockActionListener.new(button)

		button.press(Point.new(0, 0))

		assert(
			action_listener.has_been_notified_of?(
				[
					[button, true]
				]
			)
		)

		button.release(Point.new(0, 0))

		assert(
			action_listener.has_been_notified_of?(
				[
					[button, true],
					[button, false]
				]
			)
		)

		button.value=true

		button.press(Point.new(0, 0))

		assert(
			action_listener.has_been_notified_of?(
				[
					[button, true],
					[button, false],
					[button, false]
				]
			)
		)

		button.release(Point.new(0, 0))

		assert(
			action_listener.has_been_notified_of?(
				[
					[button, true],
					[button, false],
					[button, false],
					[button, true]
				]
			)
		)

	end

/*
	addTestLedEventListener { |view|
		var listener;
		listener = { |source, point, on|
			gotLed = gotLed.add( ( source: source.id, point: point, on: on ) );
		};
		view.addLedEventListener(listener);
		^listener
	}

	test_defaults {
		this.assertEqual(false, smallButton.value);
		this.assert(smallButton.isCoupled);
		this.assertEqual(\toggle, smallButton.behavior);
	}

	test_a_single_view_press_on_a_button_should_make_it_pressed {
		smallButton.press(Point.new(0, 0));
		this.assert(smallButton.isPressed);
	}

	test_a_grid_button_is_not_released_until_all_view_buttons_are_released {
		var largeButton = GRButton.newDetached(2, 2);
		largeButton.press(Point.new(0, 0));
		this.assert(largeButton.isPressed);
		largeButton.press(Point.new(1, 0));
		this.assert(largeButton.isPressed);
		largeButton.release(Point.new(0, 0));
		this.assert(largeButton.isPressed);
		largeButton.release(Point.new(1, 0));
		this.assert(largeButton.isReleased);
	}

	test_when_value_is_true_led_is_refreshed {
		this.addTestLedEventListener(smallButton);
		smallButton.value = true;
		this.assertEqual(
			[
				( source: \smallButton, point: Point.new(0, 0), on: true ),
			],
			gotLed
		)
	}

	test_a_toggle_grid_button_should_toggle_value_when_grid_button_is_pressed {
		smallButton.press(Point.new(0, 0));
		this.assertEqual(true, smallButton.value);
		smallButton.release(Point.new(0, 0));
		this.assertEqual(true, smallButton.value);
		smallButton.press(Point.new(0, 0));
		this.assertEqual(false, smallButton.value);
		smallButton.release(Point.new(0, 0));
		this.assertEqual(false, smallButton.value);
	}

	test_a_momentary_grid_button_should_toggle_value_both_when_grid_button_is_pressed_and_released {
		var button = GRButton.newMomentary(nil, nil, 2, 2);
		this.assertEqual(\momentary, button.behavior);

		this.assertEqual(false, button.value);
		button.press(Point.new(0, 0));
		this.assertEqual(true, button.value);
		button.release(Point.new(0, 0));
		this.assertEqual(false, button.value);

		button.value=true;
		this.assertEqual(true, button.value);
		button.press(Point.new(0, 0));
		this.assertEqual(false, button.value);
		button.release(Point.new(0, 0));
		this.assertEqual(true, button.value);
	}

	test_decoupled {
		var button, buttonPressReleaseEvents;
		button = GRButton.newDecoupled(nil, nil, 2, 2);
		this.assertEqual(false, button.isCoupled);

		this.assertEqual(false, button.value);
		button.press(Point.new(0, 0));
		this.assertEqual(false, button.value);
		button.release(Point.new(0, 0));
		this.assertEqual(false, button.value);

		buttonPressReleaseEvents = Array.new;
		button.buttonPressedAction = { |button|
			buttonPressReleaseEvents = buttonPressReleaseEvents.add( ( button: button, what: \pressed ) )
		};
		button.buttonReleasedAction = { |button|
			buttonPressReleaseEvents = buttonPressReleaseEvents.add( ( button: button, what: \released ) )
		};

		button.press(Point.new(0, 0));
		this.assertEqual(
			[
				( button: button, what: \pressed )
			],
			buttonPressReleaseEvents
		);
		button.press(Point.new(1, 0));
		this.assertEqual(
			[
				( button: button, what: \pressed )
			],
			buttonPressReleaseEvents
		);
		button.release(Point.new(0, 0));
		this.assertEqual(
			[
				( button: button, what: \pressed )
			],
			buttonPressReleaseEvents
		);
		button.release(Point.new(1, 0));
		this.assertEqual(
			[
				( button: button, what: \pressed ),
				( button: button, what: \released )
			],
			buttonPressReleaseEvents
		);
	}
*/
}
