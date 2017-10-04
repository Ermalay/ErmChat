package ru.ermalay.app.ermchat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.util.Log;
import android.widget.TextView;

import org.xml.sax.XMLReader;

//https://habrahabr.ru/post/166351/

public class MainActivity extends AppCompatActivity {

    TextView tv_main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_main = (TextView) findViewById(R.id.tv_main);

        setArticle("article_test_text");

    }

    void setArticle(String strArticleResId) {
        int articleResId = getResources().getIdentifier(strArticleResId, "string", getPackageName());
        String text = getString(articleResId);
        if (text == null) text = "Article not found";

        Spanned spannedText = Html.fromHtml(text, null, htmlTagHandler);
        Spannable reversedText = revertSpanned(spannedText);

        tv_main.setText(reversedText);
    }

    final Spannable revertSpanned(Spanned stext) {
        Object[] spans = stext.getSpans(0, stext.length(), Object.class);
        Spannable ret = Spannable.Factory.getInstance().newSpannable(stext.toString());
        if (spans != null && spans.length > 0) {
            for(int i = spans.length - 1; i >= 0; --i) {
                ret.setSpan(spans[i], stext.getSpanStart(spans[i]), stext.getSpanEnd(spans[i]), stext.getSpanFlags(spans[i]));
            }
        }

        return ret;
    }


    Html.TagHandler htmlTagHandler = new Html.TagHandler() {
        public void handleTag(boolean opening, String tag, Editable output,	XMLReader xmlReader) {
            Object span = null;
            if (tag.startsWith("article_")) span = new ArticleSpan(MainActivity.this, tag);
            else if ("ital".equalsIgnoreCase(tag)) span = new AppearanceSpan(
                    0xffff2020,
                    AppearanceSpan.NONE,
                    20,
                    false,
                    true,
                    false,
                    false);
            else if ("b_bold".equalsIgnoreCase(tag)) span = new AppearanceSpan(
                    0xff00ff00,
                    AppearanceSpan.NONE,
                    26,
                    true,
                    false,
                    false,
                    false);
            else if ("fake_strike".equalsIgnoreCase(tag)) span = new AppearanceSpan(
                    0xff0000ff,
                    AppearanceSpan.NONE,
                    20,
                    false,
                    false,
                    true,
                    false);
            else if (tag.startsWith("color_")) span = new ParameterizedSpan(tag.substring(6));
            if (span != null) processSpan(opening, output, span);
        }
    };



    void processSpan(boolean opening, Editable output, Object span) {
        int len = output.length();
        if (opening) {
            output.setSpan(span, len, len, Spannable.SPAN_MARK_MARK);
        } else {
            Object[] objs = output.getSpans(0, len, span.getClass());
            int where = len;
            if (objs.length > 0) {
                for(int i = objs.length - 1; i >= 0; --i) {
                    if (output.getSpanFlags(objs[i]) == Spannable.SPAN_MARK_MARK) {
                        where = output.getSpanStart(objs[i]);
                        output.removeSpan(objs[i]);
                        break;
                    }
                }
            }

            if (where != len) {
                output.setSpan(span, where, len, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }


}
