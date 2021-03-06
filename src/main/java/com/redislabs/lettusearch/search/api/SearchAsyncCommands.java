package com.redislabs.lettusearch.search.api;

import com.redislabs.lettusearch.search.SearchOptions;
import com.redislabs.lettusearch.search.SearchResults;

import io.lettuce.core.RedisFuture;

/**
 * Asynchronously executed commands for RediSearch search index.
 *
 * @param <K> Key type.
 * @param <V> Value type.
 * @author Julien Ruaux
 * @since 1.0
 */
public interface SearchAsyncCommands<K, V> {

	RedisFuture<SearchResults<K, V>> search(K index, V query);

	RedisFuture<SearchResults<K, V>> search(K index, V query, Object... options);

	RedisFuture<SearchResults<K, V>> search(K index, V query, SearchOptions<K> options);

}
