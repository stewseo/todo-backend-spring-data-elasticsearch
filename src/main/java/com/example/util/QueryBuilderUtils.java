package com.example.util;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryVariant;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import co.elastic.clients.elasticsearch.core.UpdateByQueryRequest;

public interface QueryBuilderUtils {

    public static Query termQuery(String field, String value){
        QueryVariant queryVariant = new TermQuery.Builder().caseInsensitive(true).field(field).value(value).build();
        return new Query(queryVariant);
    }

    public static Query matchQuery(String field, String query) {
        return Query.of(q -> q // variant objects in the Java API Client are implementations of a “tagged union”: they contain the identifier (or tag) of the variant they hold and the value for that variant
                .match(m -> m // Returns documents that match a provided text, number, date or boolean value. The provided text is analyzed before matching.
                        .field(field) // search field
                        .query(query) // search text
                )
        );
    }

}
