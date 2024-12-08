package board.board_spring.Exception;

import lombok.Getter;

@Getter
public enum ExceptionCode {
    BOARD_NOT_FOUND(400, "board not found"),
    REPLY_NOT_FOUND(400, "reply not found"),
    MEMBER_NOT_FOUND(400, "member not found"),
    NO_PERMISSION(400, "no permission");

    private final int status;
    private final String message;

    ExceptionCode(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
