package tw.smartroute.vpn

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import org.json.JSONArray

class MainActivity : Activity() {
    private val prefs by lazy { getSharedPreferences("smartroute_prefs", Context.MODE_PRIVATE) }
    private var language = "zh-TW"
    private var sourceUrls = mutableListOf<String>()
    private var autoSwitch = true
    private lateinit var root: LinearLayout
    private lateinit var statusText: TextView
    private lateinit var sourceText: TextView

    private val bg = Color.rgb(14, 20, 32)
    private val panel = Color.rgb(27, 36, 52)
    private val accent = Color.rgb(92, 190, 164)
    private val mainText = Color.rgb(242, 246, 250)
    private val muted = Color.rgb(165, 179, 196)

    private fun tr(tw: String, cn: String) = if (language == "zh-CN") cn else tw

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        language = prefs.getString("language", "zh-TW") ?: "zh-TW"
        autoSwitch = prefs.getBoolean("auto_switch", true)
        sourceUrls = loadSources()
        renderHome()
    }

    private fun renderHome() {
        root = baseColumn()
        val scroll = ScrollView(this).apply { addView(root) }
        setContentView(scroll)

        root.addView(text("SMARTRoute", 13, accent, true))
        root.addView(text("SmartRoute VPN", 28, mainText, true), margin(0, 4, 0, 4))
        root.addView(text(tr("多線路管理 · 自動切換 · 隱私優先", "多线路管理 · 自动切换 · 隐私优先"), 14, muted), margin(0, 0, 0, 22))

        val statusPanel = card()
        statusPanel.addView(text(tr("目前狀態", "当前状态"), 14, muted))
        statusText = text(tr("尚未連線", "尚未连接"), 24, mainText, true)
        statusPanel.addView(statusText, margin(0, 8, 0, 4))
        statusPanel.addView(text(tr("此版本尚未建立 VPN 加密通道。", "此版本尚未建立 VPN 加密通道。"), 13, muted))
        root.addView(statusPanel, margin(0, 0, 0, 14))

        root.addView(button(tr("VPN 連線核心尚未啟用", "VPN 连接核心尚未启用"), false).apply {
            setOnClickListener {
                AlertDialog.Builder(this@MainActivity)
                    .setTitle(tr("尚未連接 VPN 核心", "尚未连接 VPN 核心"))
                    .setMessage(tr("這是介面與設定原型。它不會假裝已連線；整合真正 VPN 核心和有效伺服器設定後才能建立加密通道。",
                        "这是界面与设置原型。它不会假装已连接；整合真正 VPN 核心和有效服务器配置后才能建立加密通道。"))
                    .setPositiveButton(tr("知道了", "知道了"), null).show()
            }
        }, margin(0, 0, 0, 20))

        root.addView(text(tr("線路狀態", "线路状态"), 18, mainText, true), margin(0, 0, 0, 10))
        root.addView(nodeCard(tr("主線 A", "主线路 A"), tr("尚未設定", "尚未配置"), tr("等待有效節點", "等待有效节点")), margin(0, 0, 0, 8))
        root.addView(nodeCard(tr("主線 B", "主线路 B"), tr("尚未設定", "尚未配置"), tr("等待有效節點", "等待有效节点")), margin(0, 0, 0, 8))
        root.addView(nodeCard(tr("備援 C", "备用线路 C"), tr("待命", "待命"), tr("自動切換尚未啟用", "自动切换尚未启用")), margin(0, 0, 0, 18))

        sourceText = text(sourceCountLabel(), 14, muted)
        root.addView(sourceText, margin(0, 0, 0, 10))
        root.addView(button(tr("管理訂閱來源", "管理订阅来源"), true).apply { setOnClickListener { showSourceDialog() } }, margin(0, 0, 0, 8))
        root.addView(button(tr("設定與語言", "设置与语言"), true).apply { setOnClickListener { showSettings() } }, margin(0, 0, 0, 8))
        root.addView(text(tr(
            "安全提醒：公開免費節點可能記錄流量或失效。請勿匯入來路不明的設定。",
            "安全提醒：公开免费节点可能记录流量或失效。请勿导入来源不明的配置。"
        ), 12, muted), margin(0, 18, 0, 0))
        root.addView(text("v0.2.1-prototype · " + tr("尚未提供實際 VPN 連線", "尚未提供实际 VPN 连接"), 11, muted), margin(0, 22, 0, 0))
    }

    private fun sourceCountLabel() = tr("已儲存訂閱來源：", "已保存订阅来源：") + sourceUrls.size +
        tr(" 個（尚未下載或驗證）", " 个（尚未下载或验证）")

    private fun loadSources(): MutableList<String> {
        val raw = prefs.getString("source_urls", "[]") ?: "[]"
        return try {
            val arr = JSONArray(raw)
            MutableList(arr.length()) { i -> arr.getString(i) }
        } catch (_: Exception) { mutableListOf() }
    }

    private fun saveSources() {
        val arr = JSONArray()
        sourceUrls.forEach { arr.put(it) }
        prefs.edit().putString("source_urls", arr.toString()).apply()
        sourceText.text = sourceCountLabel()
    }

    private fun showSourceDialog() {
        val input = EditText(this).apply {
            hint = "https://example.com/subscription"
            setTextColor(mainText)
            setHintTextColor(muted)
            setPadding(dp(12), dp(12), dp(12), dp(12))
        }
        val builder = AlertDialog.Builder(this)
            .setTitle(tr("新增訂閱來源", "新增订阅来源"))
            .setMessage(tr("僅接受 HTTPS 網址並儲存在本機。本版本不會下載、解析或驗證訂閱內容。",
                "仅接受 HTTPS 网址并保存在本机。本版本不会下载、解析或验证订阅内容。"))
            .setView(input)
            .setNegativeButton(tr("取消", "取消"), null)
            .setPositiveButton(tr("加入", "添加"), null)
        val dialog = builder.create()
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val value = input.text.toString().trim()
                val uri = android.net.Uri.parse(value)
                if (uri.scheme == "https" && !uri.host.isNullOrBlank() && value.length <= 2048) {
                    if (!sourceUrls.contains(value)) {
                        sourceUrls.add(value)
                        saveSources()
                        Toast.makeText(this, tr("已儲存網址；尚未連線或驗證", "已保存网址；尚未连接或验证"), Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this, tr("此網址已存在", "此网址已存在"), Toast.LENGTH_SHORT).show()
                    }
                    dialog.dismiss()
                } else {
                    input.error = tr("請輸入有效的 HTTPS 網址", "请输入有效的 HTTPS 网址")
                }
            }
        }
        dialog.show()
    }

    private fun showSettings() {
        val options = arrayOf(
            tr("語言：繁體中文（台灣）", "语言：繁体中文（台湾）"),
            tr("語言：简体中文", "语言：简体中文"),
            tr("自動切換：", "自动切换：") + tr(if (autoSwitch) "開啟" else "關閉", if (autoSwitch) "开启" else "关闭"),
            tr("節點清理規則", "节点清理规则")
        )
        AlertDialog.Builder(this)
            .setTitle(tr("設定", "设置"))
            .setItems(options) { _, which ->
                when (which) {
                    0 -> { language = "zh-TW"; prefs.edit().putString("language", language).apply(); renderHome() }
                    1 -> { language = "zh-CN"; prefs.edit().putString("language", language).apply(); renderHome() }
                    2 -> { autoSwitch = !autoSwitch; prefs.edit().putBoolean("auto_switch", autoSwitch).apply(); renderHome() }
                    3 -> AlertDialog.Builder(this)
                        .setTitle(tr("節點清理預設規則", "节点清理默认规则"))
                        .setMessage(tr(
                            "單次失敗：保留並稍後重試\n暫時異常：冷卻 30 分鐘\n確認失效：停止反覆測試，保留 24 小時\n連續 7 天失效：可自動刪除\n使用中節點：切換成功前不刪除\n收藏／手動加入節點：預設保留",
                            "单次失败：保留并稍后重试\n暂时异常：冷却 30 分钟\n确认失效：停止反复测试，保留 24 小时\n连续 7 天失效：可自动删除\n使用中节点：切换成功前不删除\n收藏／手动添加节点：默认保留"
                        ))
                        .setPositiveButton(tr("了解", "了解"), null).show()
                }
            }.show()
    }

    private fun nodeCard(title: String, state: String, detail: String): LinearLayout {
        val box = card()
        val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
        val left = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        left.addView(text(title, 16, mainText, true))
        left.addView(text(detail, 12, muted), margin(0, 4, 0, 0))
        row.addView(left, LinearLayout.LayoutParams(0, -2, 1f))
        row.addView(text(state, 13, accent, true))
        box.addView(row)
        return box
    }

    private fun baseColumn() = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(dp(20), dp(22), dp(20), dp(22))
        setBackgroundColor(bg)
    }
    private fun card() = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(dp(16), dp(15), dp(16), dp(15))
        setBackgroundColor(panel)
    }
    private fun button(label: String, enabled: Boolean) = Button(this).apply {
        text = label; isEnabled = enabled; setTextColor(if (enabled) mainText else muted)
        textSize = 14f; isAllCaps = false
        if (android.os.Build.VERSION.SDK_INT >= 21)
            backgroundTintList = android.content.res.ColorStateList.valueOf(if (enabled) Color.rgb(45, 70, 82) else Color.rgb(48, 56, 68))
        layoutParams = LinearLayout.LayoutParams(-1, dp(52))
    }
    private fun text(value: String, size: Int, color: Int, bold: Boolean = false) = TextView(this).apply {
        text = value; textSize = size.toFloat(); setTextColor(color)
        if (bold) typeface = Typeface.DEFAULT_BOLD
    }
    private fun margin(l: Int, t: Int, r: Int, b: Int) = LinearLayout.LayoutParams(-1, -2).apply {
        setMargins(dp(l), dp(t), dp(r), dp(b))
    }
    private fun dp(value: Int) = (value * resources.displayMetrics.density).toInt()
}
