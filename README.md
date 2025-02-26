这是木星 星雨盒 博客 后端SPRINGBOOT 开源版本
使用技术包含 SPRINGBOOT MybatisPlus(AutoGenerator) redis swageer fastjson
# 主要特点
弱化数据库概念： 通过扫描文件 自主生成式文件结构 可以使得即便没有数据库情况本地修改文件也可以实时预览
动态爬虫机制： 爬取的内容很容易展示在页面
权限管理机制： 特定的用户特定的权限
自主研发JULOG日志管理机制： 通过自己的JULOG日志体系 可以很方便的记录日常日志和报错日志（精确到 文件 ->行）
![image](https://github.com/user-attachments/assets/e7752333-714f-498f-bd5c-8cbd19869931)
# 项目亮点
1. 使用redis 进行用户经常访问的临时数据缓存
2. 文章存储通过NIO以MARKDOWN存储，前端展示时对MARKDOWN进行解析 用配置文件配置文件存储相关路径
3 伪删除
4 对于用户交互内容严格过滤 利用安全知识减少安全风险
5 使用nginx反向代理
