import javafx.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class CosineSimilarityCalculator
{
    /**
     * 유사도 리스트 정렬을 위한 sorter
     */
    private static Comparator<Pair<Document, Double>> pairSorter = (pair1, pair2) ->
    {
        final double VAL1 = pair1.getValue();
        final double VAL2 = pair2.getValue();

        if (VAL1 < VAL2)
            return 1;
        else if (VAL1 > VAL2)
            return -1;

        return 0;
    };

    /**
     * 사용자 임의 가중치를 사용한 유사도 계산
     * @param src 유사도를 계산할 원본 문서
     * @param targets 유사도를 계산할 목표 문서 리스트
     * @param manualWeight 유사도 계산 시 사용되는 사용자 정의 가중치 값
     * @return 목표 문서 리스트와 유사도 값의 오름차순 정렬 리스트
     */
    static List<Pair<Document, Double>> calculateWithManualWeight(
            final Document src, final List<Document> targets, final double manualWeight)
    {
        final double WEIGHT_SQ = (manualWeight * manualWeight);
        List<Pair<Document, Double>> retVal = new ArrayList<>();

        Set<Term> srcTermSet = new HashSet<>(src.getTermList());
        final double SRC_FEATURE_SUM = (Math.sqrt(srcTermSet.size()) * manualWeight); // 분모 좌항

        targets.forEach(document ->
        {
            Set<Term> targetTermSet = new HashSet<>(document.getTermList());
            final double TARGET_FEATURE_SUM = (Math.sqrt(targetTermSet.size()) * manualWeight); // 분모 우항

            List<Term> overlappedTerms = targetTermSet.stream().filter(srcTermSet::contains).collect(Collectors.toList());
            final double OVERLAPPED_FEATURE_SUM = (overlappedTerms.size() * WEIGHT_SQ); // 분자

            // 유사도 값
            final double cosSimilarity = (OVERLAPPED_FEATURE_SUM / (SRC_FEATURE_SUM * TARGET_FEATURE_SUM));
            retVal.add(new Pair<>(document, cosSimilarity));
        });

        // 오름차순 정렬
        retVal.sort(pairSorter);

        return retVal;
    }

    /**
     * TF-IDF 가중치를 이용한 유사도 계산
     * @param src 유사도를 계산할 원본 문서
     * @param targets 유사도를 계산할 목표 문서 리스트
     * @param numFeaturesForCalculating 유사도 계산에 사용할 상위 TF-IDF 가중치 개수
     * @return 목표 문서 리스트와 유사도 값의 오름차순 정렬 리스트
     */
    static List<Pair<Document, Double>> calculate(
            final Document src, final List<Document> targets, final int numFeaturesForCalculating)
    {
        List<Pair<Document, Double>> retVal = new ArrayList<>();

        TFIDFCalculator tfidfCalculator = new TFIDFCalculator(targets);

        // Term content, weight 쌍
        List<Pair<String, Double>> srcWeightList = tfidfCalculator.rank(src.getID(), numFeaturesForCalculating);
        final double[] srcFeatureSum = {0.0};
        srcWeightList.forEach(pair ->
        {
            final double VAL = pair.getValue();
            srcFeatureSum[0] += (VAL * VAL);
        });
        final double SRC_FEATURE_SUM = Math.sqrt(srcFeatureSum[0]); // 분모 좌항

        targets.forEach(document ->
        {
            List<Pair<String, Double>> targetWeightList = tfidfCalculator.rank(document.getID(), numFeaturesForCalculating);
            final double[] targetFeatureSum = {0.0};
            targetWeightList.forEach(pair ->
            {
                final double VAL = pair.getValue();
                targetFeatureSum[0] += (VAL * VAL);
            });
            final double TARGET_FEATURE_SUM = Math.sqrt(targetFeatureSum[0]); // 분모 우항

            final double[] overlappedFeatureSum = {0.0};
            targetWeightList.forEach(targetPair ->
            {
                for (Pair<String, Double> srcPair : srcWeightList)
                {
                    if (targetPair.getKey().equals(srcPair.getKey()))
                    {
                        overlappedFeatureSum[0] += (srcPair.getValue() * targetPair.getValue());
                        break;
                    }
                }
            });

            final double OVERLAPPED_FEATURE_SUM = overlappedFeatureSum[0]; // 분자

            // 유사도 값
            final double cosSimilarity = (OVERLAPPED_FEATURE_SUM / (SRC_FEATURE_SUM * TARGET_FEATURE_SUM));
            retVal.add(new Pair<>(document, cosSimilarity));
        });

        // 오름차순 정렬
        retVal.sort(pairSorter);

        return retVal;
    }
}


