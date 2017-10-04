package ru.ermalay.app.ermchat;

import android.text.style.ClickableSpan;
import android.view.View;

public class ArticleSpan extends ClickableSpan {

    final MainActivity activity;
    final String articleId;

    public ArticleSpan(MainActivity activity, String articleId) {
        super();
        this.activity = activity;
        this.articleId = articleId;
    }

    @Override
    public void onClick(View arg0) {
        activity.setArticle(articleId);
    }
}