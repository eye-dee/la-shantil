package shantil;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunctionLagrangeForm;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;

public class Main {

  private static final int DEGREE = 200;

  static final class LagrangePart {

    final PolynomialFunctionLagrangeForm form;
    final double left;
    final double right;

    private LagrangePart(
        PolynomialFunctionLagrangeForm form, double left, double right) {
      this.form = form;
      this.left = left;
      this.right = right;
    }
  }

  public static void main(String[] args) throws IOException {
    File csvData = new File("data");
    CSVParser parser = CSVParser.parse(csvData, Charset.defaultCharset(), CSVFormat.RFC4180);

    SplineInterpolator interpolator = new SplineInterpolator();

    List<Pair<Double, Double>> list = parser.getRecords()
        .stream()
        .map(c -> ImmutablePair.of(c.get(0), c.get(1)))
        .map(p -> ImmutablePair.of(Double.parseDouble(p.getLeft()),
            Double.parseDouble(p.getRight())))
        .collect(Collectors.toList());

    List<LagrangePart> parts = new ArrayList<>();
    for (var i = 0; i < list.size() / DEGREE + 1; i++) {
      List<Pair<Double, Double>> pairs = list.subList(i * DEGREE, (i + 1) * DEGREE);

      double[] x = pairs.stream()
          .mapToDouble(Pair::getLeft).toArray();
      double[] y = pairs.stream()
          .mapToDouble(Pair::getRight).toArray();

      PolynomialFunctionLagrangeForm lagrangeForm = new PolynomialFunctionLagrangeForm(
          x, y);

      parts.add(new LagrangePart(lagrangeForm, pairs.get(0).getLeft(),
          pairs.get(pairs.size() - 1).getLeft()));
    }

    PolynomialSplineFunction polygon = interpolator.interpolate(list.stream()
        .mapToDouble(Pair::getLeft).toArray(), list.stream()
        .mapToDouble(Pair::getRight).toArray());

    LineChartEx.draw(polygon,
        list.stream()
            .mapToDouble(Pair::getLeft).min().orElse(0),
        list.stream()
            .mapToDouble(Pair::getLeft).max().orElse(0));

    LineChartParts.draw(parts,
        list.stream()
            .mapToDouble(Pair::getLeft).min().orElse(0),
        list.stream()
            .mapToDouble(Pair::getLeft).max().orElse(0));
  }

}
