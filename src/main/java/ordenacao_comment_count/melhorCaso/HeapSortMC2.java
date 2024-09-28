package main.java.ordenacao_comment_count.melhorCaso;

import org.apache.commons.csv.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class HeapSortMC2 {
        public static void main(String[] args) {
            long startTime = System.currentTimeMillis();

            // Caminhos de entrada e saída ajustados
            Path inputPath = Paths.get("Diretório do csv", "videos_T1_comment_count_heapSort_medioCaso.csv");
            Path outputPath = Paths.get("Diretório do csv", "videos_T1_comment_count_heapSort_melhorCaso.csv");
            int columnIndex = 11; // Índice da coluna "comment_count"

            System.out.println("Iniciando leitura do arquivo CSV: " + inputPath.toString());
            System.out.println("Caminho absoluto: " + inputPath.toAbsolutePath());

            // Verifica se o arquivo existe antes de tentar ler
            if (!Files.exists(inputPath)) {
                System.err.println("Arquivo não encontrado: " + inputPath.toAbsolutePath());
                return;
            }

            // Carregar dados do CSV
            String[][] data = readCsv(inputPath);

            if (data != null && data.length > 1) {
                System.out.println("Arquivo lido. Iniciando limpeza dos espaços iniciais...");
                cleanSpacesInColumn(data, columnIndex);

                System.out.println("Iniciando preenchimento das linhas com menos colunas...");
                fillMissingColumns(data);

                // Verifica se já está ordenado
                if (isAlreadySorted(data, columnIndex)) {
                    System.out.println("Os dados já estão ordenados. Pulando a ordenação.");
                } else {
                    System.out.println("Iniciando ordenação...");
                    try {
                        sortByCommentCountHeapSort(data, columnIndex);
                        System.out.println("Ordenação finalizada. Salvando arquivo...");
                    } catch (IndexOutOfBoundsException e) {
                        System.err.println("Erro ao ordenar: " + e.getMessage());
                        e.printStackTrace();
                    }
                }

                // Escreve os dados no arquivo CSV de saída
                writeCsv(data, outputPath);
            } else {
                System.err.println("Dados insuficientes para ordenar ou o arquivo está vazio.");
            }

            long endTime = System.currentTimeMillis();
            Runtime runtime = Runtime.getRuntime();
            long memoryUsed = runtime.totalMemory() - runtime.freeMemory();

            System.out.println("Tempo de execução: " + (endTime - startTime) + " ms");
            System.out.println("Memória utilizada: " + (memoryUsed / (1024 * 1024)) + " MB");
        }

        // Função de ordenação HeapSort
        public static void sortByCommentCountHeapSort(String[][] data, int columnIndex) {
            if (data.length <= 1) {
                System.err.println("Lista de dados está vazia ou contém apenas o cabeçalho.");
                return;
            }

            if (columnIndex < 0 || data[0].length <= columnIndex) {
                System.err.println("Índice de coluna inválido: " + columnIndex);
                return;
            }

            String[] header = data[0];
            String[][] dataToSort = Arrays.copyOfRange(data, 1, data.length);

            System.out.println("Iniciando HeapSort para a coluna: " + columnIndex);

            // Construir o heap (reorganizar o array)
            int n = dataToSort.length;
            for (int i = n / 2 - 1; i >= 0; i--) {
                heapify(dataToSort, n, i, columnIndex); // Certifique-se de que heapify está correto para ordem crescente
            }

            // Extrair elementos do heap um por um
            for (int i = n - 1; i >= 0; i--) {
                // Move a raiz atual para o final
                String[] temp = dataToSort[0];
                dataToSort[0] = dataToSort[i];
                dataToSort[i] = temp;
                System.out.println("Elemento na posição " + i + " trocado com a raiz.");

                // Chama heapify no heap reduzido
                heapify(dataToSort, i, 0, columnIndex);
            }

            // Recolocar o cabeçalho
            String[][] sortedData = new String[data.length][];
            sortedData[0] = header;
            System.arraycopy(dataToSort, 0, sortedData, 1, dataToSort.length);
            System.arraycopy(sortedData, 0, data, 0, sortedData.length);

            System.out.println("Ordenação HeapSort concluída.");
        }


        // Função para garantir que a subárvore com raiz em i seja um heap
        private static void heapify(String[][] data, int n, int i, int columnIndex) {
            int largest = i; // Inicializa o maior como a raiz
            int left = 2 * i + 1; // Filho à esquerda
            int right = 2 * i + 2; // Filho à direita

            try {
                // Se o filho à esquerda for maior que a raiz
                if (left < n && Integer.parseInt(data[left][columnIndex].trim()) > Integer.parseInt(data[largest][columnIndex].trim())) {
                    largest = left;
                    System.out.println("Filho à esquerda " + Arrays.toString(data[left]) + " é maior que " + Arrays.toString(data[i]));
                }

                // Se o filho à direita for maior que o maior até agora
                if (right < n && Integer.parseInt(data[right][columnIndex].trim()) > Integer.parseInt(data[largest][columnIndex].trim())) {
                    largest = right;
                    System.out.println("Filho à direita " + Arrays.toString(data[right]) + " é maior que " + Arrays.toString(data[largest]));
                }

                // Se o maior não é a raiz
                if (largest != i) {
                    String[] swap = data[i];
                    data[i] = data[largest];
                    data[largest] = swap;

                    System.out.println("Trocando " + Arrays.toString(data[i]) + " com " + Arrays.toString(data[largest]));

                    // Recursivamente transforma a subárvore afetada em um heap
                    heapify(data, n, largest, columnIndex);
                }
            } catch (NumberFormatException e) {
                System.err.println("Erro ao converter para número: " + e.getMessage());
                // Você pode decidir como tratar esse erro, por exemplo, ignorar ou continuar
            }
        }


        public static boolean isAlreadySorted(String[][] data, int columnIndex) {
            for (int i = 1; i < data.length - 1; i++) {
                try {
                    // Limpar espaços extras e verificar se os valores não estão vazios ou nulos
                    String currentValueStr = data[i][columnIndex] != null ? data[i][columnIndex].trim() : "";
                    String nextValueStr = data[i + 1][columnIndex] != null ? data[i + 1][columnIndex].trim() : "";

                    // Ignorar comparações com valores vazios ou não numéricos
                    if (currentValueStr.isEmpty() || nextValueStr.isEmpty()) {
                        System.out.println("Ignorando linha " + i + " ou " + (i + 1) + " por ter valor vazio.");
                        continue;
                    }

                    int currentValue = Integer.parseInt(currentValueStr);
                    int nextValue = Integer.parseInt(nextValueStr);

                    // Se o valor atual for maior que o próximo, não está ordenado
                    if (currentValue > nextValue) {
                        System.out.println("Dados não estão ordenados na linha " + i + ": " + currentValue + " > " + nextValue);
                        return false;
                    }
                } catch (NumberFormatException e) {
                    // Ignorar linhas que não podem ser convertidas para números
                    System.out.println("Ignorando linha " + i + " devido a um erro de formato: " + data[i][columnIndex]);
                }
            }
            return true; // Já está ordenado
        }

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

                CSVFormat csvFormat = CSVFormat.DEFAULT
                        .withTrim()
                        .withQuoteMode(QuoteMode.ALL);

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
            }
            return data;
        }

        private static String[][] expandArray(String[][] original, int newSize) {
            return Arrays.copyOf(original, newSize);
        }

        public static void cleanSpacesInColumn(String[][] data, int columnIndex) {
            for (int i = 1; i < data.length; i++) {
                if (data[i][columnIndex] != null) {
                    data[i][columnIndex] = data[i][columnIndex].trim();
                }
            }
        }

        public static void fillMissingColumns(String[][] data) {
            for (int i = 1; i < data.length; i++) {
                if (data[i].length < data[0].length) {
                    String[] filledRow = Arrays.copyOf(data[i], data[0].length);
                    Arrays.fill(filledRow, data[i].length, filledRow.length, "");
                    data[i] = filledRow;
                    System.out.println("Colunas preenchidas na linha " + i + ": " + Arrays.toString(data[i]));
                }
            }
        }

        public static void writeCsv(String[][] data, Path outputPath) {
            try (CSVPrinter csvPrinter = new CSVPrinter(new FileWriter(outputPath.toFile()), CSVFormat.DEFAULT.withHeader(data[0]))) {
                for (int i = 1; i < data.length; i++) {
                    csvPrinter.printRecord((Object[]) data[i]);
                }
                System.out.println("Arquivo CSV salvo em: " + outputPath.toAbsolutePath());
            } catch (IOException e) {
                System.err.println("Erro ao escrever no arquivo: " + e.getMessage());
            }
        }
    }