# Create: Iron Suit (addon do Create, Minecraft 1.21.1 / NeoForge)

Armadura de ferro motorizada com reator, voo e repulsores, integrada ao Create.

## O que tem
- **Armadura (4 pecas)**: montada com *Sequenced Assembly* do Create a partir da armadura de ferro vanilla
  (capacete: folha de ouro + tubo eletronico; peitoral: folha de ouro + mecanismo de precisao; calcas: folha de ouro + folha de ferro; botas: folha de ouro + helice).
- **Reator (peitoral)**: guarda ate 1.000.000 FE. A barra do item mostra a carga.
- **Carregador do Reator** (bloco cinetico do Create): clique com o peitoral para inserir, mao vazia para retirar. Gire com forca cinetica para carregar (2 FE/tick por RPM, consome stress).
- **Conjunto completo + carga**: voo (gasta FE), visao noturna, forca II, resistencia II, pulo, velocidade, resistencia ao fogo, respirar na agua e queda sem dano.
- **Luva Repulsora**: segure e clique com o botao direito; raio de ate 48 blocos, 5.000 FE por disparo.

Todos os numeros ficam em `SuitConstants.java`.

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
