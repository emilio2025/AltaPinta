package com.backend.AltaPinta.controller;

import com.backend.AltaPinta.model.Cliente;
import com.backend.AltaPinta.repository.ClienteRepository;
import com.backend.AltaPinta.service.EmailService;
import com.backend.AltaPinta.service.PasswordValidator;
import com.backend.AltaPinta.dto.RegisterRequest;
import com.backend.AltaPinta.Config.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import com.backend.AltaPinta.model.Rol;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins="http://localhost:4200")
public class AuthController {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    private String generarCodigo() {
        Random rnd = new Random();
        int number = 100000 + rnd.nextInt(900000);
        return String.valueOf(number);
    }

    // Correo que recibe ROLE_ADMIN al registrarse. Se configura fuera del
    // codigo (secrets.properties o variable de entorno ADMIN_EMAIL).
    @Value("${app.admin.correo}")
    private String adminEmail;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        String nombre = req.getNombre();
        String correo = req.getCorreo();
        String password = req.getPassword();
        String direccion = req.getDireccion();
        String dni = req.getDni();

        // RF035: Validar criterios de seguridad de la contraseña
        if (!PasswordValidator.esValida(password)) {
            return ResponseEntity.badRequest().body(PasswordValidator.MENSAJE_ERROR);
        }

        // DNI ya registrado
        if (clienteRepository.existsByDni(dni)) {
            return ResponseEntity.badRequest().body("El DNI ya está registrado");
        }

        // Correo repetido
        if (clienteRepository.existsByCorreo(correo)) {
            return ResponseEntity.badRequest().body("El correo ya está registrado");
        }

        Cliente c = new Cliente();
        c.setNombre(nombre);
        c.setCorreo(correo);
        c.setDireccion(direccion);
        c.setDni(dni);
        c.setPassword(passwordEncoder.encode(password));
        c.setVerificado(false);

        String codigo = generarCodigo();
        c.setTokenVerificacion(codigo);
        c.setTokenExpira(LocalDateTime.now().plusMinutes(15));

        if (correo.equalsIgnoreCase(adminEmail)) {
            c.setRol(Rol.ADMIN);
        } else {
            c.setRol(Rol.USER);
        }


        clienteRepository.save(c);

        try {
            emailService.sendVerificationCode(correo, codigo);
        } catch (Exception ex) {
            clienteRepository.delete(c);
            return ResponseEntity.status(500).body("No se pudo enviar correo de verificación");
        }

        return ResponseEntity.ok("Registro iniciado, verifica tu correo.");
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verify(@RequestBody Map<String, String> payload) {
        String correo = payload.get("correo");
        String codigo = payload.get("codigo");

        if (correo == null || codigo == null) {
            return ResponseEntity.badRequest().body("correo y codigo requeridos");
        }

        Optional<Cliente> oc = clienteRepository.findByCorreo(correo);
        if (!oc.isPresent()) {
            return ResponseEntity.badRequest().body("Cuenta no encontrada");
        }

        Cliente c = oc.get();
        if (c.isVerificado()) {
            return ResponseEntity.badRequest().body("Cuenta ya verificada");
        }

        if (c.getTokenVerificacion() == null || c.getTokenExpira() == null) {
            return ResponseEntity.badRequest().body("No hay código de verificación registrado");
        }

        if (LocalDateTime.now().isAfter(c.getTokenExpira())) {
            return ResponseEntity.badRequest().body("El código ha expirado, solicita uno nuevo");
        }

        if (!c.getTokenVerificacion().equals(codigo)) {
            return ResponseEntity.status(401).body("Código incorrecto");
        }

        c.setVerificado(true);
        c.setTokenVerificacion(null);
        c.setTokenExpira(null);
        clienteRepository.save(c);

        return ResponseEntity.ok("Cuenta verificada. Ahora puedes iniciar sesión.");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> payload) {
        String correo = payload.get("correo");
        String password = payload.get("password");

        if (correo == null || password == null) {
            return ResponseEntity.badRequest().body("correo y password requeridos");
        }

        Optional<Cliente> oc = clienteRepository.findByCorreo(correo);
        if (!oc.isPresent()) {
            return ResponseEntity.status(401).body("Credenciales inválidas");
        }

        Cliente c = oc.get();

        // Verificación deshabilitada para inicio de sesión
        // if (!c.isVerificado()) return ResponseEntity.status(401).body("Cuenta no verificada");

        if (!passwordEncoder.matches(password, c.getPassword())) {
            return ResponseEntity.status(401).body("Credenciales inválidas");
        }

        String token = jwtUtil.generateToken(correo);

        // El rol viaja en la respuesta para que el frontend sepa que menus
        // mostrar. Antes no se enviaba, asi que saveAuthData guardaba
        // undefined y la interfaz decidia quien era administrador comparando
        // el correo con una constante suya: dos fuentes de verdad que se
        // separaban en cuanto se ascendia a alguien en la base de datos.
        //
        // Esto NO es lo que autoriza nada: cada peticion vuelve a leer el rol
        // de la base a traves de CustomUserDetailsService. Es solo para la
        // interfaz.
        return ResponseEntity.ok(Map.of(
                "token", token,
                "nombre", c.getNombre(),
                "correo", c.getCorreo(),
                "rol", c.getRol().name()
        ));
    }


}
