package it.zeze.fantaformazioneweb.bean.util;

import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;

import it.zeze.fanta.service.bean.MessageResponse;
import it.zeze.fanta.service.bean.MessageSeverity;
import it.zeze.fanta.service.bean.ServiceResponse;

public class Utility {

	public static void convertServiceResponseToStatusMessage(StatusMessages messages, ServiceResponse serviceResponse) {
		if (serviceResponse != null && serviceResponse.getMessageResponse() != null && !serviceResponse.getMessageResponse().isEmpty()) {
			for (MessageResponse currentMessage : serviceResponse.getMessageResponse()) {
				if (currentMessage.getSeverity() != null) {
					if (currentMessage.getSeverity().equals(MessageSeverity.INFO)) {
						messages.add(Severity.INFO, currentMessage.getMessage());
					}
					if (currentMessage.getSeverity().equals(MessageSeverity.ERROR)) {
						messages.add(Severity.ERROR, currentMessage.getMessage());
					}
					if (currentMessage.getSeverity().equals(MessageSeverity.WARN)) {
						messages.add(Severity.WARN, currentMessage.getMessage());
					}
				}
			}
		}
	}

}
