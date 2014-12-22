package com.github.premnirmal.ticker.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.github.premnirmal.ticker.R;
import com.github.premnirmal.ticker.StocksApp;
import com.github.premnirmal.ticker.model.IStocksProvider;
import com.github.premnirmal.ticker.network.Suggestion;
import com.github.premnirmal.ticker.network.SuggestionApi;
import com.github.premnirmal.ticker.network.Suggestions;

import java.util.List;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by premnirmal on 12/21/14.
 */
public class TickerSelectorActivity extends ActionBarActivity {

    @Inject
    SuggestionApi suggestionApi;

    @Inject
    IStocksProvider stocksProvider;

    Subscription subscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((StocksApp) getApplicationContext()).inject(this);
        setContentView(R.layout.stock_search_layout);

        final EditText searchView = (EditText) findViewById(R.id.query);
        final ListView listView = (ListView) findViewById(R.id.resultList);

        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                final String query = s.toString().trim().replaceAll(" ", "");
                if (!query.isEmpty()) {
                    if (subscription != null) {
                        subscription.unsubscribe();
                    }
                    subscription = suggestionApi.getSuggestions(query)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .doOnError(new Action1<Throwable>() {
                                @Override
                                public void call(Throwable throwable) {
                                    Toast.makeText(TickerSelectorActivity.this,
                                            throwable.getMessage(), Toast.LENGTH_SHORT);
                                }
                            })
                            .subscribe(new Action1<Suggestions>() {
                                @Override
                                public void call(Suggestions suggestions) {
                                    final List<Suggestion> suggestionList = suggestions.ResultSet.Result;
                                    listView.setAdapter(new SuggestionsAdapter(suggestionList));
                                }
                            });
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final SuggestionsAdapter suggestionsAdapter = (SuggestionsAdapter) parent.getAdapter();
                final Suggestion suggestion = suggestionsAdapter.getItem(position);
                final String ticker = suggestion.isStock() ? suggestion.symbol
                        : ("^" + suggestion.symbol);
                stocksProvider.addStock(ticker);
                finish();
            }
        });

    }


}