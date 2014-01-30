/**

 * This method creates valid XML from the validation scores.
 * @return A string containing the different validation scores.

public String toXML(){
        String content = "";
content = content + "    <validation>\n";
content = content + "        <columnConfidence>" + clusterCertainty + "</columnConfidence>\n";
content = content + "        <mostFrequentlyNumberOfClusters>" + mostFrequentNumberOfClusters+"</mostFrequentlyNumberOfClusters>\n";
content = content + "        <highestAmountOfClusters>" + highestAmountOfClusters + "</highestAmountOfClusters>\n";
content = content + "        <highestAmountOfClustersOccurrences>" + highestAmountOfClustersOccurrences + "</highestAmountOfClustersOccurrences>\n";
content = content + "        <clusterThreshold>" + lineThreshold+"</clusterThreshold>\n";
content = content + "        <cellsWithMissingDataAdded>" + cellsWithMissingDataAdded +"</cellsWithMissingDataAdded>\n";
if(cellsWithMissingDataAdded > 0){
        content = content + "        <cellsWithMissingDataAddedScores>" +CommonMethods.changeIllegalXMLCharacters(cellsWithMissingDataAddedObjects.toString()) + "</cellsWithMissingDataAddedScores>\n";
}
        content = content + "        <averageDistanceBetweenRows>" + averageDistanceBetweenRows + "</averageDistanceBetweenRows>\n";
if(titleConfidence.size() > 0){
        content = content + "        <titleConfidence>" + titleConfidence + "</titleConfidence>\n";
}
        content = content + "        <falsePositive>" + falsePositive + "</falsePositive>\n";
content = content + "    </validation>\n";
return content;
}*/