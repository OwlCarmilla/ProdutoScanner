package pt.ipt.dam.stockapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import pt.ipt.dam.stockapp.ui.screens.*
import pt.ipt.dam.stockapp.ui.viewmodel.AuthViewModel
import pt.ipt.dam.stockapp.ui.viewmodel.StockViewModel

/**
 * Rotas da aplicação
 */
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Login : Screen("login")
    object Register : Screen("register")
    object Verify : Screen("verify")
    object Scanner : Screen("scanner")
    object ProductDetail : Screen("product/{productId}") {
        fun createRoute(productId: Int) = "product/$productId"
    }
    object About : Screen("about")
}

/**
 * Navegação principal da aplicação
 */
@Composable
fun StockNavigation(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = hiltViewModel(),
    stockViewModel: StockViewModel = hiltViewModel()
) {
    val authState by authViewModel.uiState.collectAsState()
    
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        // Ecrã Principal
        composable(Screen.Home.route) {
            HomeScreen(
                stockViewModel = stockViewModel,
                authViewModel = authViewModel,
                onNavigateToLogin = { navController.navigate(Screen.Login.route) },
                onNavigateToScanner = { navController.navigate(Screen.Scanner.route) },
                onNavigateToProduct = { productId ->
                    stockViewModel.selectProduto(productId)
                    navController.navigate(Screen.ProductDetail.createRoute(productId))
                },
                onNavigateToAbout = { navController.navigate(Screen.About.route) }
            )
        }
        
        // Login
        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onNavigateToVerify = { navController.navigate(Screen.Verify.route) },
                onLoginSuccess = { navController.popBackStack() }
            )
        }
        
        // Registo
        composable(Screen.Register.route) {
            RegisterScreen(
                viewModel = authViewModel,
                onNavigateToLogin = { navController.popBackStack() },
                onNavigateToVerify = { navController.navigate(Screen.Verify.route) }
            )
        }
        
        // Verificação de código
        composable(Screen.Verify.route) {
            VerifyScreen(
                viewModel = authViewModel,
                onVerifySuccess = {
                    navController.popBackStack(Screen.Home.route, inclusive = false)
                },
                onBack = { navController.popBackStack() }
            )
        }
        
        // Scanner de código de barras
        composable(Screen.Scanner.route) {
            ScannerScreen(
                stockViewModel = stockViewModel,
                onBarcodeScanned = { barcode ->
                    stockViewModel.scanBarcode(barcode)
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }
        
        // Detalhe do produto
        composable(Screen.ProductDetail.route) {
            ProductDetailScreen(
                stockViewModel = stockViewModel,
                authViewModel = authViewModel,
                onBack = {
                    stockViewModel.clearSelection()
                    navController.popBackStack()
                },
                onNavigateToLogin = { navController.navigate(Screen.Login.route) }
            )
        }
        
        // Sobre/Créditos
        composable(Screen.About.route) {
            AboutScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
