package com.capstone.recycle.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public void broadcastBinStatus(Long deviceId, Object binStatusResponse) {
        messagingTemplate.convertAndSend("/topic/devices/" + deviceId + "/bins", binStatusResponse);
    }

    public void broadcastTrashEvent(Object trashEventResponse) {
        messagingTemplate.convertAndSend("/topic/events", trashEventResponse);
    }

    public void broadcastError(Object errorLogResponse) {
        messagingTemplate.convertAndSend("/topic/errors", errorLogResponse);
    }
}