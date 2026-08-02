import { Routes } from '@angular/router';

import { ProductoComponent } from './productos/productos';
import { MenuComponent } from './menu/menu';
import { LoginComponent } from './login/login';
import { RegisterComponent } from './register/register.component';
import { DetalleProductoComponent } from './detalle-producto/detalle-producto';
import { FavoritosComponent } from './favoritos/favoritos';
import { PrincipalComponent } from './principal/principal';
import { ForgotPasswordComponent } from './forgot-password/forgot-password';
import { ResetPasswordComponent } from './reset-password/reset-password';
import { CarritoComponent } from './carrito/carrito';
import { CheckoutComponent } from './checkout/checkout';
import { PedidosComponent  } from './pedido/pedido';
import { AdminDashboardComponent } from './admin/admin';
import { MiPerfil } from './mi-perfil/mi-perfil';
import { AgregarTarjetaComponent } from './agregar-tarjeta/agregar-tarjeta';
import { TarjetasComponent } from './tarjetas/tarjetas';
import { DireccionesComponent } from './direcciones/direcciones';

import { AuthGuard } from './guards/auth.guard';
import { AdminGuard } from './guards/admin.guard';

export const routes: Routes = [
  { path: '', redirectTo: 'principal', pathMatch: 'full' },

  // públicas
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'principal', component: PrincipalComponent },

  // protegidas para cualquier usuario logueado
  { path: 'menu', component: MenuComponent, canActivate: [AuthGuard] },
  { path: 'detalle/:id', component: DetalleProductoComponent, canActivate: [AuthGuard] },
  { path: 'favoritos', component: FavoritosComponent, canActivate: [AuthGuard] },
  // Las páginas de categoría son el mismo catálogo con un filtro puesto.
  // Antes cada una era un componente propio que repetía toda la pantalla
  // (unas 2.400 líneas entre las cuatro) y solo cambiaba el título y el
  // banner. Ahora reutilizan MenuComponent, que lee la categoría de la
  // ruta. Las URL no cambian, así que los enlaces existentes siguen
  // funcionando.
  { path: 'varon', component: MenuComponent, canActivate: [AuthGuard], data: { categoria: 'Varon' } },
  { path: 'mujer', component: MenuComponent, canActivate: [AuthGuard], data: { categoria: 'Mujer' } },
  { path: 'nino',  component: MenuComponent, canActivate: [AuthGuard], data: { categoria: 'Niños' } },
  { path: 'bebe',  component: MenuComponent, canActivate: [AuthGuard], data: { categoria: 'Bebé' } },
  { path: 'forgot-password', component: ForgotPasswordComponent },
  { path: 'reset-password', component: ResetPasswordComponent },
  { path: 'carrito', component: CarritoComponent, canActivate: [AuthGuard] },
  { path: 'checkout', component: CheckoutComponent, canActivate: [AuthGuard] },
  { path: 'pedido', component: PedidosComponent, canActivate: [AuthGuard] },
  { path: 'perfil', component: MiPerfil, canActivate: [AuthGuard] },
  { path: 'agregarTarjeta', component: AgregarTarjetaComponent, canActivate: [AuthGuard] },
  { path: 'tarjetas', component: TarjetasComponent, canActivate: [AuthGuard] },
  { path: 'direcciones', component: DireccionesComponent, canActivate: [AuthGuard] },

  // solo admin (correo específico)
  { path: 'admin', component: AdminDashboardComponent, canActivate: [AdminGuard] },
  { path: 'productos', component: ProductoComponent, canActivate: [AdminGuard] },

  // fallback
  { path: '**', redirectTo: 'menu' }
];
