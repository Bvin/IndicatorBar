# IndicatorBar
SeekBar的简单实现，Thumb移动的距离是固定，原始SeekBar为了让让Thumb站的下会修剪Thumb移动的距离。
```
<declare-styleable name="IndicatorBar">
        <attr name="trackThickness" format="dimension"/> //track的厚度
        <attr name="themeColor" format="reference|color"/> //主题颜色
        <attr name="highlightColor" format="reference|color"/> //高亮颜色
        <attr name="thumb" format="reference" /> //滑块图片
        <attr name="highlightThumb" format="reference" /> //高亮滑块图片
        <attr name="textSize" format="dimension" /> //字体大小
        <attr name="highlightTextSize" format="dimension" /> //高亮字体的大小
        <attr name="currentPosition" format="integer" /> //当前选中的position
        <attr name="maxPosition" format="integer" /> //最大值
        <attr name="indicatorOffset" format="integer" /> //indicator偏移
        <attr name="lowlightSelectedText" format="string" /> //普通说明文字
        <attr name="maxHighlightSelectedText" format="string" /> //高亮最大值说明文字
        <attr name="showTicks" format="boolean" />
</declare-styleable>
```
以上对应的都有对应的java方法设置..
```
  final IndicatorBar ib = (IndicatorBar) findViewById(R.id.indicatorBar1);
  ib.setOnIndicatorChangeListener(new OnIndicatorChangeListener() {
            
      @Override
      public void onIndicatorChanged(IndicatorBar indicatorBar, int position, float xAtPosition) {
            Log.i("position"+position, "xAtPosition"+xAtPosition);
         }
      });
  ib.setHightlightIndicators(new int[]{3,4,5});//高亮indicator配置，在下图中就是亮色的数字
```
![第一页](https://raw.githubusercontent.com/Bvin/IndicatorBar/master/assets/Screenshot.jpeg)
