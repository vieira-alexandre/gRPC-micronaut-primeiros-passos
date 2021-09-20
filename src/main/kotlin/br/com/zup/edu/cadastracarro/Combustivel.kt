package br.com.zup.edu.cadastracarro

enum class Combustivel(i: Int) {
    DESCONHECIDO(0),
    GASOLINA(1),
    ALCOOL(2),
    FLEX(3);

    val codigo: Int = i;

    companion object {
        fun obterCombustivelPeloCodigo(codigo: Int): Combustivel {
            for (i in values()) {
                if (i.codigo == codigo) {
                    return i;
                }
            }

            throw IllegalArgumentException("Código inválido")
        }
    }
}