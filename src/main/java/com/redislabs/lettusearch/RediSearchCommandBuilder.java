package com.redislabs.lettusearch;

import static com.redislabs.lettusearch.CommandType.ADD;
import static com.redislabs.lettusearch.CommandType.CREATE;
import static com.redislabs.lettusearch.CommandType.DROP;
import static com.redislabs.lettusearch.CommandType.SEARCH;
import static com.redislabs.lettusearch.CommandType.SUGADD;
import static com.redislabs.lettusearch.CommandType.SUGGET;

import java.util.List;

import com.redislabs.lettusearch.search.Document;
import com.redislabs.lettusearch.search.DropOptions;
import com.redislabs.lettusearch.search.Schema;
import com.redislabs.lettusearch.search.SearchNoContentOutput;
import com.redislabs.lettusearch.search.SearchOptions;
import com.redislabs.lettusearch.search.SearchOutput;
import com.redislabs.lettusearch.search.SearchResults;
import com.redislabs.lettusearch.suggest.GetOptions;
import com.redislabs.lettusearch.suggest.SuggestOutput;
import com.redislabs.lettusearch.suggest.SuggestResult;
import com.redislabs.lettusearch.suggest.Suggestion;

import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.internal.LettuceAssert;
import io.lettuce.core.output.CommandOutput;
import io.lettuce.core.output.IntegerOutput;
import io.lettuce.core.output.StatusOutput;
import io.lettuce.core.protocol.BaseRedisCommandBuilder;
import io.lettuce.core.protocol.Command;
import io.lettuce.core.protocol.CommandArgs;

/**
 * Dedicated pub/sub command builder to build pub/sub commands.
 *
 * @author Mark Paluch
 * @since 4.2
 */
public class RediSearchCommandBuilder<K, V> extends BaseRedisCommandBuilder<K, V> {

	static final String MUST_NOT_BE_NULL = "must not be null";
	static final String MUST_NOT_BE_EMPTY = "must not be empty";

	public RediSearchCommandBuilder(RedisCodec<K, V> codec) {
		super(codec);
	}

	public Command<K, V, String> add(String index, Document document) {
		LettuceAssert.notNull(index, "index " + MUST_NOT_BE_NULL);
		LettuceAssert.notEmpty(index, "index " + MUST_NOT_BE_EMPTY);
		LettuceAssert.notNull(document, "document " + MUST_NOT_BE_NULL);
		CommandArgs<K, V> args = new RediSearchCommandArgs<>(codec).add(index);
		document.build(args);
		return createCommand(ADD, new StatusOutput<>(codec), args);
	}

	public Command<K, V, String> create(String index, Schema schema) {
		LettuceAssert.notNull(index, "index " + MUST_NOT_BE_NULL);
		LettuceAssert.notEmpty(index, "index " + MUST_NOT_BE_EMPTY);
		LettuceAssert.notNull(schema, "schema " + MUST_NOT_BE_NULL);
		CommandArgs<K, V> args = new RediSearchCommandArgs<>(codec).add(index);
		schema.build(args);
		return createCommand(CREATE, new StatusOutput<>(codec), args);
	}

	public Command<K, V, String> drop(String index, DropOptions options) {
		LettuceAssert.notNull(index, "index " + MUST_NOT_BE_NULL);
		CommandArgs<K, V> args = new RediSearchCommandArgs<>(codec).add(index);
		options.build(args);
		return createCommand(DROP, new StatusOutput<>(codec), args);
	}

	public Command<K, V, SearchResults<K, V>> search(String index, String query, SearchOptions options) {
		LettuceAssert.notNull(index, "index " + MUST_NOT_BE_NULL);
		LettuceAssert.notNull(query, "query " + MUST_NOT_BE_NULL);
		CommandArgs<K, V> args = new RediSearchCommandArgs<>(codec).add(index).add(query);
		options.build(args);
		return createCommand(SEARCH, getSearchOutput(codec, options), args);
	}

	private CommandOutput<K, V, SearchResults<K, V>> getSearchOutput(RedisCodec<K, V> codec, SearchOptions options) {
		if (options.isNoContent()) {
			return new SearchNoContentOutput<>(codec, options);
		}
		return new SearchOutput<>(codec, options);
	}

	public Command<K, V, Long> add(String key, Suggestion suggestion) {
		LettuceAssert.notNull(key, "key " + MUST_NOT_BE_NULL);
		LettuceAssert.notNull(suggestion, "suggestion " + MUST_NOT_BE_NULL);
		CommandArgs<K, V> args = new RediSearchCommandArgs<>(codec).add(key);
		suggestion.build(args);
		return createCommand(SUGADD, new IntegerOutput<>(codec), args);
	}

	public Command<K, V, List<SuggestResult<V>>> get(K key, V prefix, GetOptions options) {
		LettuceAssert.notNull(key, "key " + MUST_NOT_BE_NULL);
		LettuceAssert.notNull(prefix, "prefix " + MUST_NOT_BE_NULL);
		CommandArgs<K, V> args = new RediSearchCommandArgs<>(codec).addKey(key).addValue(prefix);
		options.build(args);
		return createCommand(SUGGET, new SuggestOutput<>(codec, options), args);
	}

	protected <T> Command<K, V, T> createCommand(CommandType type, CommandOutput<K, V, T> output,
			CommandArgs<K, V> args) {
		return new Command<K, V, T>(type, output, args);
	}

}