package com.rione.notification.application.port.out;

import com.rione.common.application.OutPort;

@OutPort
public interface PostEventListener {

	void listen(String payload);
}
