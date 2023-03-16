package fi.hel.allu.search.util;


import co.elastic.clients.elasticsearch.ElasticsearchClient;
import org.elasticsearch.client.RestHighLevelClient;

/**
 * preparation for migration from high rest to API.
 *
 */
public class ClientWrapper {

    private RestHighLevelClient hlrc;
    private ElasticsearchClient esClient;

    public ClientWrapper(RestHighLevelClient hlrc, ElasticsearchClient esClient) {
        this.hlrc = hlrc;
        this.esClient = esClient;
    }

    public RestHighLevelClient getHlrc() {
        return hlrc;
    }

    public void setHlrc(RestHighLevelClient hlrc) {
        this.hlrc = hlrc;
    }

    public ElasticsearchClient getEsClient() {
        return esClient;
    }

    public void setEsClient(ElasticsearchClient esClient) {
        this.esClient = esClient;
    }
}