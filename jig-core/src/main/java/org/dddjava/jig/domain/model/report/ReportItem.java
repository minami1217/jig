package org.dddjava.jig.domain.model.report;

/**
 * 一覧出力項目
 *
 * 出力項目の名前と並び順の定義。
 */
public enum ReportItem {
    クラス名,
    メソッド名,
    メソッド戻り値の型,

    イベントハンドラ,

    クラス和名,
    メソッド和名,
    メソッド戻り値の型の和名,
    メソッド引数の型の和名,

    使用箇所数,
    使用箇所,

    メソッド数,
    メソッド一覧,

    分岐数,

    // なるべく使わない
    汎用文字列,
    汎用真偽値
}
