package com.rione.notification.application.port.out;

import com.rione.common.application.OutPort;

@OutPort
public interface SocialEventListener {

	void listen(String payload);
}
