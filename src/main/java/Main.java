import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.stream.Collectors;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;

public class Main {

  public static void main(String[] args) throws IOException {
    var csvData = new File("data");
    var parser = CSVParser.parse(csvData, Charset.defaultCharset(), CSVFormat.RFC4180);

    var interpolator = new SplineInterpolator();

    var list = parser.getRecords()
        .stream()
        .map(c -> ImmutablePair.of(c.get(0), c.get(1)))
        .map(p -> ImmutablePair.of(Double.parseDouble(p.getLeft()),
            Double.parseDouble(p.getRight())))
        .collect(Collectors.toList());

    var polygon = interpolator.interpolate(list.stream()
        .mapToDouble(ImmutablePair::getLeft).toArray(), list.stream()
        .mapToDouble(ImmutablePair::getRight).toArray());

    LineChartEx.draw(polygon);
  }

}
