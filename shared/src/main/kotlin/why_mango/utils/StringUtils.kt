package why_mango.utils

/**
 * 앞 뒤 4 글자를 제외한 나머지 글자를 마스킹 처리합니다.
 * 길이가 짧으면 모든 글자를 마스킹 처리합니다.
 */
fun String.mask(): String = when {
        length <= 8 -> "*".repeat(length)
        else -> "${substring(0, 4)}${"*".repeat(length - 8)}${substring(length - 4)}"
    }