package why_mango.transaction

import org.springframework.stereotype.Component
import why_mango.enums.ApiProvider


@Component
class TransactionServiceFactory(
    private val services: List<TransactionService>,
) {
    fun get(apiProvider: ApiProvider): TransactionService = services.first { it.apiProvider == apiProvider }
}