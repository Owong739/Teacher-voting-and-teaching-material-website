package hkmu.wadd.exception;

import java.util.UUID;

public class AttachmentNotFound extends Exception {
    public AttachmentNotFound(UUID id) {
        super("Attachment " + id + " does not exist.");
    }
}