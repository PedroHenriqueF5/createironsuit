# Create: Iron Suit (addon do Create, Minecraft 1.21.1 / NeoForge)

Armadura de ferro motorizada com reator, voo e repulsores, integrada ao Create.

## O que tem
- **5 armaduras** (cada uma com modelo 3D proprio, status e habilidades): Classica (vermelho/dourado), Furtiva (preta, rapida, pula alto),
  Cobalto (azul/prata, voo mais rapido), Prata (resistente) e **Titan** (pesada, modelo grande, muita defesa e forca, voo lento).
- Cada armadura e montada com *Sequenced Assembly* do Create a partir de uma armadura vanilla (ferro, diamante ou netherite).
- **Reator (peitoral)**: guarda ate 1.000.000 FE. **Carregador do Reator** (bloco cinetico do Create) recarrega com rotacao.
- **Conjunto completo + carga**: voo, visao noturna, forca, resistencia, pulo, velocidade, respirar na agua, fogo e queda sem dano.
- **Luva Repulsora**: botao direito com ela na mao.

## Teclas (mude em Controles > Create: Iron Suit)
| Tecla | Poder | Custo (FE) |
|---|---|---|
| R | Repulsor (raio das maos) | 5.000 |
| G | Unibeam (raio do peito, dano alto) | 40.000 |
| Z | Pulso de energia (empurra e fere ao redor) | 15.000 |
| V | Impulso / dash na direcao do olhar | 8.000 |
| B | Ligar/desligar voo | - |
| H | Ligar/desligar sistemas (efeitos passivos) | - |

Todos os numeros ficam em `SuitConstants.java` e `SuitVariant.java`.

## Como compilar
1. JDK 21.
2. Em `gradle.properties`, fixe `create_version` com uma versao real de
   https://maven.createmod.net/com/simibubi/create/create-1.21.1/ (o padrao `6.0.+` pega a mais recente da serie 6.0).
3. `./gradlew build` (Windows: `gradlew.bat build`) -> o jar sai em `build/libs/`.
4. Para testar no jogo: `./gradlew runClient`.
5. No Minecraft: instale o jar junto com Create 6.0.x (e Ponder/Flywheel que ele pede) no NeoForge 21.1.x.

## Texturas
As texturas sao placeholders simples. Troque os PNGs em `src/main/resources/assets/createironsuit/textures/`
(armadura: `models/armor/iron_suit_layer_1.png` e `_layer_2.png`, formato vanilla 64x32).
