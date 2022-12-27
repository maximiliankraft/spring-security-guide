package at.maxkraft.restsec.permission;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PermissionType {
    full(4L, "full"),
    grant(3L, "grant"),
    write(2L, "write"),
    read(1L, "read"),
    none(0L, "none");

    final Long powerLevel;
    final String permissionName;

    public static PermissionType fromString(String s){
        return switch (s) {
            case "full" -> PermissionType.full;
            case "grant" -> PermissionType.grant;
            case "write" -> PermissionType.write;
            case "read" -> PermissionType.read;
            default -> PermissionType.none;
        };
    }
}
