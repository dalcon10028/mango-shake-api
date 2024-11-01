package why_mango.user.enums

enum class Role(val privileges: Set<Privilege>) {
    GUEST(setOf(Privilege.DASHBOARD_AUTHORITY)),
    USER(setOf(Privilege.DASHBOARD_AUTHORITY, Privilege.WALLET_AUTHORITY)),
    ADMIN(setOf(Privilege.DASHBOARD_AUTHORITY, Privilege.WALLET_AUTHORITY, Privilege.ADMIN_AUTHORITY)),
}