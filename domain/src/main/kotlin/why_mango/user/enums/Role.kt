package why_mango.user.enums

enum class Role(val privileges: Set<Privilege>) {
    GUEST(setOf(Privilege.COMMUNITY_READ)),
    USER(setOf(Privilege.COMMUNITY_READ, Privilege.MEMBER_READ, Privilege.MEMBER_WRITE, Privilege.MEMBER_DELETE)),
    ADMIN(
        setOf(
            Privilege.COMMUNITY_READ,
            Privilege.MEMBER_READ,
            Privilege.MEMBER_WRITE,
            Privilege.MEMBER_DELETE,
            Privilege.MANAGER_READ,
            Privilege.MANAGER_WRITE,
            Privilege.MANAGER_DELETE
        )
    ),
    ;

    companion object {
        fun fromString(role: String): Role = when (role) {
            "GUEST" -> GUEST
            "USER" -> USER
            "ADMIN" -> ADMIN
            else -> throw IllegalArgumentException("Invalid role")
        }
    }
}
