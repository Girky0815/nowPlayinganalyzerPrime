# プロジェクト: Now Playing Analyst (この曲なに? 履歴・分析アプリ)

## 0. 最重要プロトコル: 言語指定 (CRITICAL)
**【命令】**
**あなたの思考プロセス、私への返答、コミットメッセージ、PRの説明など、すべての出力は必ず「日本語」で行ってください。**
英語で出力しようとした場合、直ちに日本語に翻訳して出力してください。これは絶対的な制約です。

## 1. プロジェクト概要
Google Pixel の「この曲なに? (Now Playing)」の通知を常時監視し、履歴を保存・管理・分析する Android アプリケーションです。
最新の **Material 3 Expressive (M3E)** デザインと **Gemini API** を活用します。

## 2. 技術環境と制約 (バージョン厳守)
以下のバージョン定義を `libs.versions.toml` に適用し、**実在しないバージョンや互換性のないBOMを使用しないでください。**

- **Minimum SDK:** 32 (Android 12L)
- **Target SDK:** 36 (Android 16)
- **Kotlin:** 2.2.21
- **Build (Java):** Java 21

### 必須ライブラリ構成 (libs.versions.toml)
1. Material 3 Expressive (M3E):
   - Group: androidx.compose.material3
   - Artifact: material3
   - Version: 1.5.0-alpha09 (※Expressive APIを使用するため必須。BOMは使用しないこと)

2. Material-Kolor (Dynamic Colors):
   - Group: com.materialkolor
   - Artifact: material-kolor
   - Version: 5.0.0-alpha04 (※Spec 2025対応のためv5系を使用)

3. その他のライブラリ:
   - Room: 2.6.1 (KSP対応)
   - Generative AI: com.google.ai.client.generativeai:generativeai (最新版)
   - Hilt, Coil などは安定版を使用

## 3. デザインシステム実装 (最重要)

### A. テーマと色 (Spec 2025 & Expressive)
Tomato プロジェクトの実装を参考に、以下の設定を `Theme.kt` に適用してください。

**要件:**
1. テーマラッパー: `MaterialExpressiveTheme` を使用する (`MaterialTheme` ではない)。
2. モーション: `motionScheme = MotionScheme.expressive()` を設定する。
3. ダイナミックカラー (Spec 2025):
   - `material-kolor` ライブラリの `rememberDynamicColorScheme` を使用する。
   - **重要:** `specVersion` パラメータに必ず `ColorSpec.SpecVersion.SPEC_2025` を指定する。
   - パレットスタイル: `PaletteStyle.TonalSpot`
   - 色の役割: 背景色には `SurfaceContainerHigh`、カード/リスト項目には `SurfaceBright` を採用する。

**実装ヒント (Theme.kt):**

    val dynamicColorScheme = rememberDynamicColorScheme(
      seedColor = seedColor,
      isDark = darkTheme,
      style = PaletteStyle.TonalSpot,
      specVersion = ColorSpec.SpecVersion.SPEC_2025 // ここが重要
    )

    MaterialExpressiveTheme(
      colorScheme = dynamicColorScheme,
      typography = Typography,
      motionScheme = MotionScheme.expressive(), // Expressiveモーションを有効化
      content = content
    )

### B. タイポグラフィ (可変フォント)
- フォントファイル: `R.font.google_sans_flex` (`res/font/` に配置済みと仮定)
- 実装ロジック:
  - Compose の `FontVariation` API を使用する。
  - **強調箇所 (曲名など):** `FontVariation.weight(700)` に加え、**`FontVariation.axis("ROND", 100f)`** を適用して丸みを帯びさせる。
  - 通常テキスト: 標準設定。
  - フォールバック: Noto Sans JP。

## 4. アプリケーション機能要件

### 機能 A: 通知取得 (Notification Listener)
- `NotificationListenerService` を継承したサービスを実装。
- ターゲットパッケージ: `com.google.intelligence.sense` (Pixel Ambient Services)。
- 取得データ: 通知の `extras` から 曲名 (Title) と アーティスト名 (Text/Artist) を抽出。
- **重複除外ロジック:** 直前の検知から一定時間（例: 10分）以内で、かつ同じ曲である場合はDBに保存しない。

### 機能 B: 履歴 UI (Expressive List)
- **UIコンポーネント:** M3E のリストコンポーネント、または `SurfaceBright` 色のカードを使用した `LazyColumn`。
- **グループ化:** 日付ごとにデータをグループ化して表示。
- **ヘッダー:** 月が変わるごとに折りたたみ可能なヘッダーを表示（例: "2025年10月 (計120曲)"）。

### 機能 C: 詳細分析 (Gemini API)
- 設定画面で APIキー と モデル (Gemini 2.0 / 2.5 / 3.0 Pro) を選択可能にする。
- **分析機能:** 「場所×曲」「時間帯×ジャンル」などの傾向を分析し、詳細画面にリッチなテキストで表示する。

## 5. Antigravity 開発ワークフロー
1. **依存関係のセットアップ:** まず `libs.versions.toml` を指定されたバージョン（特に material3 alphaと material-kolor v5）で書き換える。
2. **テーマ基盤の構築:** 上記の「実装ヒント」を参考にして `Theme.kt` を作成し、Spec 2025 と M3E が動作することを確認する。
3. **機能実装:** 通知リスナー → DB → UI の順に実装を進める。
4. **ハルシネーション禁止:** ライブラリのバージョンを勝手に捏造しないこと。

## 6. 外部情報の検証プロトコル (Search & Validation)
**【重要】 ライブラリのバージョンやAPIの仕様について、あなたの内部知識のみに頼ることを禁止します。**

1. **検索の義務化:**
   - `libs.versions.toml` を更新する際や、新しいライブラリを導入する際は、必ず **Google Search** または **Maven Central / Google Maven Repository** を検索し、**実在する最新バージョン**であることを確認してください。
   - 「たぶんこのバージョンだろう」という推測は**禁止**です。

2. **ドキュメントの確認:**
   - Material 3 Expressive や Material-Kolor のような新しいライブラリを使用する場合、コードを書く前に最新の公式ドキュメントやリポジトリの README を検索・参照してください。
   - 古い `MaterialTheme` の実装方法と混同しないように注意してください。

3. **エラー解決時:**
   - ビルドエラーが発生した場合、エラーメッセージをそのまま検索にかけて解決策を探してください。

## 7. コード規約
1. **コードスタイル:** スペース数は2とする。
2. **コメント:** 日本語で処理や関数にコメントを付ける。