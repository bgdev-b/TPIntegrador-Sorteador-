package services;

import java.time.Month;
import java.time.Year;
import java.util.HashSet;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

public class SorteoService {

    private static final int MAX_TICKET = 9999;

    private final Random random = new Random();
    private final Set<String> numerosGanadores = new HashSet<>();
    private final Year anio = Year.now();

    public void reiniciar() {
        numerosGanadores.clear();
    }

    public int capacidad(Month mes) {
        Objects.requireNonNull(mes, "El mes no puede ser null");
        return mes.length(anio.isLeap()) * MAX_TICKET;
    }

    public String generarGanadorUnico(Month mes) {
        if (numerosGanadores.size() >= capacidad(mes)) {
            throw new IllegalStateException(
                    "Ya se generaron todos los números posibles para " + mes);
        }

        String numero;
        do {
            numero = generarNumeroSorteo(generarDiaRandom(mes), mes, generarTicketRandom());
        } while (!numerosGanadores.add(numero));

        return numero;
    }

    public String generarNumeroSorteo(int dia, Month mes, int ticket) {
        Objects.requireNonNull(mes, "El mes no puede ser null");

        int maxDia = mes.length(anio.isLeap());

        if (dia < 1 || dia > maxDia) {
            throw new IllegalArgumentException(
                    "Día inválido: " + dia + ". Para " + mes + " debe estar entre 1 y " + maxDia);
        }
        if (ticket < 1 || ticket > MAX_TICKET) {
            throw new IllegalArgumentException(
                    "Ticket inválido: " + ticket + ". Debe estar entre 1 y " + MAX_TICKET);
        }

        return String.format("%02d%02d%04d", dia, mes.getValue(), ticket);
    }

    private int generarDiaRandom(Month mes) {
        Objects.requireNonNull(mes, "El mes no puede ser null");
        return random.nextInt(mes.length(anio.isLeap())) + 1;
    }

    private int generarTicketRandom() {
        return random.nextInt(MAX_TICKET) + 1;
    }
}