package gcd.testing.firstproject.dollar.data.datasource

import gcd.testing.firstproject.dollar.data.dao.DollarDao
import gcd.testing.firstproject.dollar.data.entity.DollarEntity
import gcd.testing.firstproject.dollar.domain.model.DollarModel

class DollarLocalDataSource (
    val dao: DollarDao
) {
    suspend fun getList(): List<DollarModel> {
        return dao.getList().map {
            it.toModel()
        }
    }
    suspend fun insert(dollar: DollarModel) {
        dao.insert(dollar.toEntity())
    }

    private fun DollarEntity.toModel() : DollarModel {
        return DollarModel(
            official = dollarOfficial?: "",
            parallel = dollarParallel?:""
        )
    }

    private fun DollarModel.toEntity() = DollarEntity(
        dollarOfficial = this.official,
        dollarParallel = this.parallel
    )
}