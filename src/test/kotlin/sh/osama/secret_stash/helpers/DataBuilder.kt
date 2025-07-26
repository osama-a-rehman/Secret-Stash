package sh.osama.secret_stash.helpers

import sh.osama.secret_stash.user.model.UserModel

fun anUniqueString(prefix: String = "", suffix: String = "") = "$prefix-${java.util.UUID.randomUUID()}-$suffix"

fun aUser(
    withUsername: String = anUniqueString("username"),
    withPassword: String = anUniqueString("password"),
    withName: String = anUniqueString("name"),
): UserModel = UserModel(
    username = withUsername,
    password = withPassword,
    name = withName,
)