# 上海公交实时查询
一款轻量的第三方公交实时到站信息查询工具

## [最新版本下载链接](https://github.com/SeanChao/SH-Bus/releases/latest)

## 真·一本正经的食用方法
1. 点击首页右下角悬浮按钮(FloatingActionButton)，在弹出的窗口(AlertDialog)中选择使用id或站名查询。
1. 通常而言，在公交车站牌上会有一个二维码，扫描后能够得到一个链接，链接形如http://webapp.shbustong.com:56008/MobileWeb/ForecastChange.aspx?stopid=bsq022，本应用中使用的站点的id即是链接中最后的几位数，所有0需要保留。
1. 基于id查询，点击OK按钮后会进入id对应站点的全部公交车的列表页。支持下拉刷新，点击公交车会在屏幕底部弹出剩余时间。点击右上角的加号可将此车站保存到侧栏。
1. 基于站名查询时，必须输入精确的站名。随后会弹出具有同样站名的全部公交车列表页，并显示其精确地址。地址信息由官方提供，其实吧……这地址看看就好……
1. 首页即收藏页，支持拖动排序，左划项目取消收藏。
1. 点击侧栏某车站进入相应页面后，左划收藏车站。此页面不支持拖动排序。

## GIF
![](https://github.com/SeanChao/SH-Bus/tree/master/gif/1mainpage.gif)
![](https://github.com/SeanChao/SH-Bus/tree/master/gif/2ctrlD.gif)
![](https://github.com/SeanChao/SH-Bus/tree/master/gif/3ctrlAltD.gif)
![](https://github.com/SeanChao/SH-Bus/tree/master/gif/4delete.gif)
![](https://github.com/SeanChao/SH-Bus/tree/master/gif/5change.gif)
![](https://github.com/SeanChao/SH-Bus/tree/master/gif/6.gif)
