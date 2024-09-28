package main.java.ordenacao_channel_title.piorCaso;

import org.apache.commons.csv.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class CountingSortPC {

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();  // Registra o tempo de início para calcular a duração.

        // Define os caminhos de entrada e saída para os arquivos CSV.
        Path inputPath = Paths.get("Diretório do csv", "videos_T1_channel_title_countingSort_medioCaso.csv");
        Path outputPath = Paths.get("Diretório do csv", "videos_T1_channel_title_countingSort_piorCaso.csv");
        int columnIndex = 4; // Índice da coluna "channel_title" que será usada na ordenação.

        // Exibe o caminho do arquivo a ser processado.
        System.out.println("Iniciando leitura do arquivo CSV: " + inputPath.toString());
        System.out.println("Caminho absoluto: " + inputPath.toAbsolutePath());

        // Verifica se o arquivo existe, caso contrário, encerra o programa.
        if (!Files.exists(inputPath)) {
            System.err.println("Arquivo não encontrado: " + inputPath.toAbsolutePath());
            return;
        }

        // Lê o arquivo CSV.
        String[][] data = readCsv(inputPath);

        if (data != null && data.length > 1) {
            // Limpa espaços em branco no início dos títulos.
            System.out.println("Arquivo lido. Iniciando limpeza dos espaços iniciais...");
            cleanSpacesInColumn(data, columnIndex);

            // Exibe os valores da coluna antes da ordenação.
            System.out.println("Valores da coluna channel_title antes da ordenação:");
            for (int i = 1; i < data.length; i++) {
                System.out.println(data[i][columnIndex]);
            }

            // Preenche linhas com menos colunas do que o esperado.
            System.out.println("Iniciando preenchimento das linhas com menos colunas...");
            fillMissingColumns(data);

            // Verifica se os dados já estão ordenados em ordem decrescente.
            if (isAlreadySortedDescending(data, columnIndex)) {
                System.out.println("Os dados já estão ordenados em ordem decrescente. Pulando a ordenação.");
            } else {
                // Realiza a ordenação usando Counting Sort.
                System.out.println("Iniciando ordenação por Counting Sort...");
                try {
                    sortByChannelTitleCountingDescending(data, columnIndex);
                    System.out.println("Ordenação por Counting Sort finalizada. Salvando arquivo...");
                } catch (IndexOutOfBoundsException e) {
                    System.err.println("Erro ao ordenar: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            // Escreve os dados ordenados no arquivo de saída.
            writeCsv(data, outputPath);
        } else {
            System.err.println("Dados insuficientes para ordenar ou o arquivo está vazio.");
        }

        // Exibe o tempo de execução e o uso de memória.
        long endTime = System.currentTimeMillis();
        Runtime runtime = Runtime.getRuntime();
        long memoryUsed = runtime.totalMemory() - runtime.freeMemory();

        System.out.println("Tempo de execução: " + (endTime - startTime) + " ms");
        System.out.println("Memória utilizada: " + (memoryUsed / (1024 * 1024)) + " MB");
    }

    // Função que realiza a ordenação por Counting Sort com base na coluna 'channel_title' em ordem decrescente.
    public static void sortByChannelTitleCountingDescending(String[][] data, int columnIndex) {
        System.out.println("Executando Counting Sort na coluna: " + columnIndex);
        if (data.length <= 1) {
            System.err.println("Lista de dados está vazia ou contém apenas o cabeçalho.");
            return;
        }

        if (columnIndex < 0 || data[0].length <= columnIndex) {
            System.err.println("Índice de coluna inválido: " + columnIndex);
            return;
        }

        // Inicializa os arrays para contar títulos únicos.
        int uniqueCount = 0;
        String[] uniqueTitles = new String[data.length - 1]; // Para armazenar títulos únicos.
        int[] counts = new int[data.length - 1]; // Para armazenar a contagem de cada título.

        // Conta a ocorrência de cada título na coluna especificada.
        for (int i = 1; i < data.length; i++) {
            String title = data[i][columnIndex].trim();
            boolean found = false;

            // Verifica se o título já foi contado.
            for (int j = 0; j < uniqueCount; j++) {
                if (uniqueTitles[j].equalsIgnoreCase(title)) {
                    counts[j]++;
                    found = true;
                    break;
                }
            }

            // Se o título for novo, adiciona-o à lista de títulos únicos.
            if (!found) {
                uniqueTitles[uniqueCount] = title;
                counts[uniqueCount] = 1;
                uniqueCount++;
            }
        }

        // Exibe a contagem de títulos únicos.
        System.out.println("Contagem dos títulos únicos:");
        for (int i = 0; i < uniqueCount; i++) {
            System.out.println("Título: " + uniqueTitles[i] + ", Contagem: " + counts[i]);
        }

        // Ordena os títulos únicos em ordem decrescente.
        String[] sortedKeys = Arrays.copyOf(uniqueTitles, uniqueCount);
        Arrays.sort(sortedKeys, (a, b) -> b.compareToIgnoreCase(a)); // Ordenação decrescente.

        // Prepara o array ordenado.
        String[][] sortedData = new String[data.length][data[0].length]; // Alocando espaço para todas as colunas.
        sortedData[0] = data[0]; // Copia o cabeçalho.

        // Preenche o array ordenado com os dados correspondentes.
        int index = 1; // Começa após o cabeçalho.
        for (String key : sortedKeys) {
            System.out.println("Processando chave: " + key);
            for (int i = 1; i < data.length; i++) {
                if (data[i][columnIndex].trim().equalsIgnoreCase(key)) {
                    if (index < sortedData.length) { // Verifica se ainda há espaço.
                        sortedData[index++] = data[i];
                        System.out.println("Adicionando '" + data[i][columnIndex] + "' na posição " + index);
                    } else {
                        System.err.println("Index ultrapassou o tamanho do sortedData.");
                    }
                }
            }
        }

        // Copia o array ordenado de volta para o array original.
        System.arraycopy(sortedData, 0, data, 0, sortedData.length);
        System.out.println("Ordenação concluída. Dados ordenados:");
        for (int i = 1; i < data.length; i++) {
            System.out.println(data[i][columnIndex]);
        }
    }

    // Função para verificar se os dados estão ordenados em ordem decrescente pela coluna especificada.
    public static boolean isAlreadySortedDescending(String[][] data, int columnIndex) {
        for (int i = 1; i < data.length - 1; i++) {
            if (data[i][columnIndex].compareToIgnoreCase(data[i + 1][columnIndex]) < 0) {
                return false;  // Se algum valor estiver em ordem crescente, retorna falso.
            }
        }
        return true; // Retorna verdadeiro se todos os valores estiverem em ordem decrescente.
    }

    // Função para ler o arquivo CSV e retornar os dados como um array 2D.
    public static String[][] readCsv(Path filePath) {
        String[][] data = new String[0][];
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()))) {
            String headerLine = reader.readLine();
            if (headerLine == null) {
                throw new IllegalArgumentException("Arquivo CSV vazio ou cabeçalho ausente.");
            }

            String[] headers = headerLine.split(",");
            data = new String[1][headers.length];
            data[0] = headers;

            System.out.println("Cabeçalhos encontrados: " + Arrays.toString(headers));

            CSVFormat csvFormat = CSVFormat.DEFAULT.withTrim().withQuoteMode(QuoteMode.ALL);
            CSVParser csvParser = new CSVParser(reader, csvFormat);
            int rowCount = 1;
            for (CSVRecord record : csvParser) {
                if (record.size() < headers.length) {
                    System.out.println("Linha ignorada por não ter colunas suficientes.");
                    continue;
                }
                data = expandArray(data, rowCount + 1);
                data[rowCount] = record.stream().toArray(String[]::new);
                System.out.println("Linha " + rowCount + " lida: " + Arrays.toString(data[rowCount]));
                rowCount++;
            }
            System.out.println("Total de linhas lidas: " + rowCount);
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.err.println("Erro de formatação: " + e.getMessage()); e.printStackTrace(); } return data; }
    // Função para expandir o tamanho de um array 2D.
    public static String[][] expandArray(String[][] array, int newSize) {
        if (newSize > array.length) {
            String[][] newArray = Arrays.copyOf(array, newSize);
            for (int i = array.length; i < newSize; i++) {
                newArray[i] = new String[array[0].length];
            }
            return newArray;
        }
        return array;
    }

    // Função para remover espaços em branco no início da coluna especificada.
    public static void cleanSpacesInColumn(String[][] data, int columnIndex) {
        for (int i = 1; i < data.length; i++) {
            data[i][columnIndex] = data[i][columnIndex].trim();
        }
    }

    // Função para preencher colunas vazias em linhas incompletas.
    public static void fillMissingColumns(String[][] data) {
        for (int i = 1; i < data.length; i++) {
            if (data[i].length < data[0].length) {
                data[i] = Arrays.copyOf(data[i], data[0].length);
            }
        }
    }

    // Função para escrever os dados ordenados em um arquivo CSV.
    public static void writeCsv(String[][] data, Path filePath) {
        try (FileWriter writer = new FileWriter(filePath.toFile())) {
            for (String[] row : data) {
                writer.write(String.join(",", row) + "\n");
            }
            System.out.println("Arquivo CSV salvo com sucesso em: " + filePath.toString());
        } catch (IOException e) {
            System.err.println("Erro ao salvar o arquivo CSV: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
