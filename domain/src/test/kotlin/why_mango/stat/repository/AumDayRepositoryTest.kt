package why_mango.stat.repository

import io.kotest.core.spec.style.FunSpec
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest
import java.time.LocalDate

@DataR2dbcTest
class AumDayRepositoryTest(
    private val aumDayRepository: AumDayRepository,
): FunSpec({
    test("findWalletSnapshotAum") {
        aumDayRepository.findWalletSnapshotAum(LocalDate.now(), LocalDate.now())
    }
})