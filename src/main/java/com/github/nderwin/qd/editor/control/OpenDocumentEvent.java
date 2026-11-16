package com.github.nderwin.qd.editor.control;

import java.nio.file.Path;

public record OpenDocumentEvent(
        Path path,
        String content
        ) {

}
