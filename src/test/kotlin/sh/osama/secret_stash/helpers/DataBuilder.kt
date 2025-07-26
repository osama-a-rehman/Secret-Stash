package sh.osama.secret_stash.helpers

import sh.osama.secret_stash.notes.model.NoteModel
import sh.osama.secret_stash.user.model.UserModel
import java.time.Instant

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

fun aNote(
    withTitle: String = anUniqueString("title"),
    withContent: String = anUniqueString("content"),
    withExpiry: Instant? = null,
    withUser: UserModel,
): NoteModel = NoteModel(
    title = withTitle,
    content = withContent,
    expiry = withExpiry,
    user = withUser,
)