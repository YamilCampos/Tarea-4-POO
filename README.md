# Consola de Videojuegos (Java, POO)

Consola modular por terminal para **2 jugadores** que integra tres juegos
(Conecta 4, Memoria Numérica y Battleship) bajo una misma interfaz, con un
**Top 3 independiente por juego**.

## Compilar y ejecutar

Requiere JDK 17 o superior (usa `switch` con flechas).

```bash
./compilar.sh                # Linux / macOS
compilar.bat                 # Windows
```

O manualmente desde la raíz del proyecto:

```bash
mkdir -p out
javac -encoding UTF-8 -d out $(find src -name '*.java')
java -Dstdout.encoding=UTF-8 -cp out consola.ConsolaVideojuegos
```

> `-encoding UTF-8` es necesario porque el código de Battleship usa `ñ` en un
> identificador (`getTamaño`) y emojis. `-Dstdout.encoding=UTF-8` evita
> caracteres rotos en la salida.

## Estructura de paquetes

```
src/
├── consola/                    ← Núcleo de la consola
│   ├── Jugable.java            Interfaz (contrato de todo juego)
│   ├── ConsolaVideojuegos.java Clase principal: menú de 3 opciones
│   ├── BattleshipAdapter.java  Adaptador del juego del compañero
│   ├── Top3.java               Podio de 3 puestos (uno por juego)
│   ├── Entrada.java            Scanner único compartido + validación
│   └── EntradaCerradaException.java
├── juegos/
│   ├── conecta4/               Conecta4 (Jugable) + TableroConecta4
│   └── memoria/                MemoriaNumerica (Jugable)
├── battleship/                 ← CÓDIGO DEL COMPAÑERO, SIN MODIFICAR
│   ├── BattleshipGame.java
│   ├── Tablero.java
│   ├── Barco.java
│   └── EstadisticaBattleship.java
├── main/                       ← Capa de soporte (ver "Dependencias")
│   ├── Jugador.java
│   └── Pantalla.java
└── stats/
    └── GestorEstadisticas.java ← Puente de resultados de Battleship
```

Dependencias entre paquetes: `consola` conoce a los juegos solo para
registrarlos; los juegos dependen de `consola.Jugable` (y `Entrada`);
`battleship` no depende de nada de `consola`.

## 1. La interfaz `Jugable` y el polimorfismo

```java
public interface Jugable {
    int start();                         // juega una partida y devuelve el puntaje
    String getNombreJuego();             // nombre para menús y estadísticas
    default String getNombreGanador() { return null; }  // opcional
}
```

- `start()` ejecuta una partida completa de 2 jugadores y devuelve un `int`
  (mayor = mejor; 0 = sin puntaje).
- `getNombreGanador()` es un método `default`: los juegos que no lo necesitan
  no lo implementan. Solo Battleship lo sobrescribe, porque su juego original
  ya pregunta el nickname al ganador y la consola así evita pedirlo dos veces.

**Polimorfismo:** `ConsolaVideojuegos` guarda sus juegos en un
`Map<Jugable, Top3>` y ejecuta `juego.start()` sin saber si es Conecta 4,
Memoria o el adaptador de Battleship. Para agregar un juego nuevo basta con
implementar `Jugable` y agregar **una línea** en `registrarJuegos()`; ni el
menú, ni el flujo de puntajes, ni las estadísticas cambian.

## 2. Juegos propios

**Conecta 4** (`juegos.conecta4`): tablero 6x7 (`TableroConecta4` encapsula la
matriz). Turnos alternados X / O; validación de columna llena; al colocar una
ficha se revisan las 4 direcciones (horizontal, vertical y ambas diagonales)
contando fichas a ambos lados. Puntaje = `100 − 5 × (movimientos del ganador − 4)`,
con mínimo 15; un empate vale 10.

**Memoria Numérica** (`juegos.memoria`): cada ronda agrega un dígito aleatorio a
la secuencia compartida. Al jugador del turno se le muestra unos segundos
(el tiempo crece con el largo), se oculta con saltos de línea y debe reescribirla.
Quien falla pierde. Puntaje = rondas completadas × 10.

## 3. Sistema Top 3 (`Top3`)

Cada juego tiene su propia instancia. `clasifica(puntaje)` devuelve `true` si el
puntaje es positivo y hay un puesto libre o supera **estrictamente** al último;
ante un empate gana el registro más antiguo. `registrar(nombre, puntaje)` inserta
en la posición correcta, desplaza a los demás y descarta al último. Si no hay
partidas registradas, la consola carga un podio de prueba (`Demo-Ana`,
`Demo-Luis`, `Demo-Sofía`). Los puntajes viven en memoria (no se persisten
al cerrar el programa, porque no era un requisito).

## 4. Integración de Battleship con el Patrón Adaptador

### El problema

`BattleshipGame` expone `void iniciarPartida(Scanner)`: no implementa `Jugable`,
no devuelve ningún puntaje y gestiona por sí mismo sus pantallas y su registro de
récords. Como **no se puede modificar**, la solución es adaptarlo desde fuera.

### Roles del patrón

| Rol         | Clase                                    |
|-------------|------------------------------------------|
| Target      | `consola.Jugable`                        |
| Adaptee     | `battleship.BattleshipGame` (intacto)    |
| Adapter     | `consola.BattleshipAdapter`              |

### Cómo funciona

`BattleshipAdapter` contiene (composición) un `BattleshipGame` y traduce:

1. `start()` llama a `juego.iniciarPartida(Entrada.scanner())`, pasándole el
   **Scanner compartido** de la consola (dos `Scanner` sobre `System.in` perderían entradas).
2. Al terminar, el juego original llama a `GestorEstadisticas.esRecordBattleship(duracion, barcosVivos)`.
   Esa clase (ver abajo) **captura** esos datos.
3. El adaptador los lee y los convierte en un `int`:
   `puntaje = barcosVivos × 100 + max(0, 300 − segundos)`.
   Sigue el mismo criterio que el `compareTo` del compañero (más barcos vivos y
   menos tiempo = mejor).
4. Cualquier `RuntimeException` del juego original (por ejemplo `InputMismatchException`
   si se escribe una letra donde espera un número) se captura: el adaptador limpia
   la línea pendiente y devuelve `0` para que la consola no se caiga.
5. El adaptador evita el doble pedido de nombre: recibe de la consola un `IntPredicate`
   (`top3::clasifica`) y el juego solo ofrece "Entras al Top 3" cuando el puntaje
   realmente clasifica. Ese nickname se expone con `getNombreGanador()`, así que la
   consola lo usa directamente.

### Dependencias que faltaban (`main` y `stats`)

El zip entregado por el compañero **no incluye** tres clases que `BattleshipGame`
importa: `main.Jugador`, `main.Pantalla` y `stats.GestorEstadisticas`. Sin ellas no
compila. Se implementaron en este proyecto, **fuera** del paquete `battleship`,
respetando exactamente las firmas que el juego usa:

| Clase                          | Firma que usa BattleshipGame                                   |
|--------------------------------|---------------------------------------------------------------|
| `main.Jugador`                 | `Jugador(String)`, `getNickname()`, `setNickname(String)`      |
| `main.Pantalla`                | `limpiar()`, `pausar(Scanner)`                                 |
| `stats.GestorEstadisticas`     | `esRecordBattleship(long, int)`, `registrarStatBattleship(EstadisticaBattleship)` |

`GestorEstadisticas` es además el **punto de acople**: como el juego devuelve `void`,
su único canal de salida es esta clase, y la consola lo aprovecha para capturar el resultado.

> Si el compañero te pasa sus versiones reales de `main` y `stats`, puedes reemplazar
> las de este proyecto, siempre que el adaptador siga pudiendo obtener la duración y
> los barcos vivos. Si cambian, solo hay que ajustar `BattleshipAdapter`, nunca
> el juego.

### Verificación de que el código original no se tocó

Los cuatro archivos de `src/battleship/` son idénticos a los del zip original
(comprobado con `diff -r`).

## Buenas prácticas aplicadas

- **Encapsulamiento:** matriz del tablero privada, `Top3.Registro` inmutable, lista del podio expuesta solo como vista no modificable.
- **Interfaces y polimorfismo:** la consola depende de `Jugable`, nunca de juegos concretos.
- **Patrón Adaptador:** integración sin modificar código ajeno.
- **Bajo acoplamiento:** un único `Scanner` en `Entrada`; validación de enteros reutilizable.
- **Robustez:** entradas inválidas no rompen el programa; Ctrl+D cierra con gracia.
