
FOTA demo操作流程：
1.（framework层）启动服务，会得到初始化结果，如果失败，以下皆不可以进行操作。
2.（framework层）采集硬件信息并检查model更新，如果有更新，界面会有提示可进行下载的model列表。
3.切换应用（应用层）绑定FOTA服务，即可获得更新的model列表。
4.（应用层）下载model列表，两个应用皆会显示下载进度（PS：实际中下载进度无需关心）。
5.（应用层）下载完成界面会有提示等待安装的model，选择安装，此时是模拟安装过程，进度更新完成后，SDK会对结果进行上报。
6.回滚操作，进入单独的页面进行回滚设置。

注意：配置文件的修改，只有修改modelCurrentVersion，model信息改变。
systemCurrentVersion这个值在demo中未进行保存，所以建议系统当前版本设置后没必要修改。
model列表根据服务器上传的model信息修改。
配置文件的位置存放在（/mnt/sdcard/autoai/deviceInfo.txt）
