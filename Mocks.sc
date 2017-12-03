MockActionListener {
	var <view, <listener, <notifications;

	*new { |view|
		^super.new.initMockActionListener(view);
	}

	initMockActionListener { |argView|
		view = argView;
		listener = this.prCreateListener;
		view.addAction(listener, this.prSelector);
		notifications = Array.new;
	}

	removeListener {
		view.removeAction(listener, this.prSelector);
	}

	hasBeenNotifiedOf { |array| ^notifications == array }

	hasNotBeenNotifiedOfAnything { ^notifications.isEmpty }

	prCreateListener {
		^{ |... args| notifications = notifications.add( args ) }
	}

	prSelector { ^\action }
}

MockViewLedRefreshedListener : MockActionListener {
	prCreateListener {
		^{ |source, point, on|
			notifications = notifications.add(
 				( source: source.id, point: point, on: on )
			)
		}
	}
	prSelector { ^\viewLedRefreshedAction }
}

MockViewButtonStateChangedListener : MockActionListener {
	prCreateListener {
		^{ |point, pressed|
			notifications = notifications.add(
				( point: point, pressed: pressed )
			)
		}
	}
	prSelector { ^\viewButtonStateChangedAction }
}

MockViewWasEnabledListener : MockActionListener {
	prSelector { ^\viewWasEnabledAction }
}

MockViewWasDisabledListener : MockActionListener {
	prSelector { ^\viewWasDisabledAction }
}

MockButtonPressedListener : MockActionListener {
	prSelector { ^\buttonPressedAction }
}

MockButtonReleasedListener : MockActionListener {
	prSelector { ^\buttonReleasedAction }
}

MockTogglePressedListener : MockActionListener {
	prSelector { ^\togglePressedAction }
}

MockToggleReleasedListener : MockActionListener {
	prSelector { ^\toggleReleasedAction }
}

MockToggleValuePressedListener : MockActionListener {
	prSelector { ^\toggleValuePressedAction }
}

MockToggleValueReleasedListener : MockActionListener {
	prSelector { ^\toggleValueReleasedAction }
}

MockToggleRangePressedListener : MockActionListener {
	prSelector { ^\toggleRangePressedAction }
}

MockLitView : GRView {
	*new { |parent, origin, numCols=nil, numRows=nil, enabled=true|
		^super.new(parent, origin, numCols, numRows, enabled).init;
	}

	init {
		isLitAtFunc = { |point| true }
	}
}

MockUnlitView : GRView {
	*new { |parent, origin, numCols=nil, numRows=nil, enabled=true|
		^super.new(parent, origin, numCols, numRows, enabled).init;
	}

	init {
		isLitAtFunc = { |point| false }
	}
}

MockOddColsLitView : GRView {
	*new { |parent, origin, numCols=nil, numRows=nil, enabled=true|
		^super.new(parent, origin, numCols, numRows, enabled).init;
	}

	init {
		isLitAtFunc = { |point| point.x % 2 == 1 }
	}
}

MockLitContainerView : GRContainerView {
	*new { |parent, origin, numCols=nil, numRows=nil, enabled=true, press_through=false|
		^super.new(parent, origin, numCols, numRows, enabled).init;
	}

	init {
		isLitAtFunc = { |point| true }
	}
}

MockOddColsLitContainerView : GRContainerView {
	*new { |parent, origin, numCols=nil, numRows=nil, enabled=true|
		^super.new(parent, origin, numCols, numRows, enabled).init;
	}

	init {
		isLitAtFunc = { |point| point.x % 2 == 1 }
	}
}

MockController : GRController {
	var <viewButtonStateChangedNotifications, <viewLedRefreshedNotifications;
	var registerNotifications;

	*new { |numCols=nil, numRows=nil, view=nil, origin=nil, createTopViewIfNoneIsSupplier=true|
		^super.new(numCols, numRows, view, origin, createTopViewIfNoneIsSupplier).init;
	}

	init {
		viewButtonStateChangedNotifications = [];
		viewLedRefreshedNotifications = [];
		registerNotifications = false;
		this.refresh;
		registerNotifications = true;
	}

	handleViewButtonStateChangedEvent { |point, pressed|
 		if (registerNotifications) {
			viewButtonStateChangedNotifications = viewButtonStateChangedNotifications.add(
				( point: point, pressed: pressed )
			);
		}
	}

	handleViewLedRefreshedEvent { |point, on|
 		if (registerNotifications) {
			viewLedRefreshedNotifications = viewLedRefreshedNotifications.add(
				( point: point, on: on )
			);
		}
	}
}

MockNotePressedListener : MockActionListener {
	prSelector { ^\notePressedAction }
}

MockNoteReleasedListener : MockActionListener {
	prSelector { ^\noteReleasedAction }
}

MockButtonValueChangedListener : MockActionListener {
	prCreateListener {
		^{ |view, x, y, value|
			notifications = notifications.add(
				( view: view, x: x, y: y, val: value )
			)
		}
	}
	prSelector { ^\buttonValueChangedAction }
}

MockToggleValueChangedListener : MockActionListener {
	prCreateListener {
		^{ |view, i, value|
			notifications = notifications.add(
				( view: view, i: i, val: value )
			)
		}
	}
	prSelector { ^\toggleValueChangedAction }
}

AbstractMockListener {
	var notifications;

	*new { |view=nil|
		^super.new.init(view);
	}

	init { |view|
		if (view.notNil) {
			this.addListener(view)
		};
		this.clearNotifications;
	}

	wasNotifiedOf { |ary|
		^notifications == ary
	}

	wasNotifiedOfAnything {
		^notifications.isEmpty
	}

	clearNotifications {
		notifications = Array.new
	}
}

MockLedEventListener : AbstractMockListener {
	var view;
	var listener;

	addListener { |argView|
		view = argView;
		listener = { |source, point, on|
			notifications = notifications.add( ( source: source.id, point: point, on: on ) )
		};
		view.addLedEventListener(listener);
	}

	removeListener {
		view.removeLedEventListener(listener);
	}
}

MockButtonEventListener : AbstractMockListener {
	var view;
	var listener;

	addListener { |argView|
		view = argView;
		listener = { |point, pressed|
			notifications = notifications.add( ( point: point, pressed: pressed ) )
		};
		view.addButtonEventListener(listener);
	}

	removeListener {
		view.removeButtonEventListener(listener)
	}
}
