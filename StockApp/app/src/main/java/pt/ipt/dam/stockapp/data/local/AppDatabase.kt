package pt.ipt.dam.stockapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import pt.ipt.dam.stockapp.data.local.dao.HistoricoDao
import pt.ipt.dam.stockapp.data.local.dao.ProdutoDao
import pt.ipt.dam.stockapp.data.local.dao.UserSessionDao
import pt.ipt.dam.stockapp.data.local.entity.HistoricoEntity
import pt.ipt.dam.stockapp.data.local.entity.ProdutoEntity
import pt.ipt.dam.stockapp.data.local.entity.UserSessionEntity

/**
 * Base de dados Room para cache local
 */
@Database(
    entities = [
        ProdutoEntity::class,
        HistoricoEntity::class,
        UserSessionEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun produtoDao(): ProdutoDao
    abstract fun historicoDao(): HistoricoDao
    abstract fun userSessionDao(): UserSessionDao
    
    companion object {
        const val DATABASE_NAME = "stock_app_db"
    }
}
