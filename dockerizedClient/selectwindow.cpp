#include "selectwindow.h"

#include "selectwindow.h"

SelectWindow::SelectWindow(QWidget *parent)
    : QWidget(parent)
{
    /* QGridLayout* mainLayout;
    QComboBox* mainSelector;
    QLabel* desc;
    QPixmap* selectedImage;
    QGraphicsView* mainView; */
    mainLayout = new QGridLayout(this);

    QGraphicsScene* scene = new QGraphicsScene();

    selectedImage = new QPixmap("fnf.jpg","jpg");

    if (selectedImage->isNull()) {
        qDebug() << "Placeholder Image failed to load\n";
    }
    else {
        QGraphicsPixmapItem* item = new QGraphicsPixmapItem(*selectedImage);
        scene->addItem(item);
        scene->setSceneRect(selectedImage->rect());
    }
    mainView = new QGraphicsView(scene);

    mainSelector = new QComboBox(this);
    mainSelector->setFixedWidth(400);

    nm1 = new QNetworkAccessManager(this);
    nm2 = new QNetworkAccessManager(this);
    connect(nm1, &QNetworkAccessManager::finished, this, [=](QNetworkReply *reply) {
        if (reply->error() == QNetworkReply::NoError) {
            QByteArray responseData = reply->readAll();
            QJsonParseError* errstate = new QJsonParseError();
            QJsonDocument data = QJsonDocument::fromJson(responseData,errstate);
            if (errstate->error != QJsonParseError::NoError) {
                qDebug() << "Failed to receive JSON with object list due to " << errstate->errorString() << "\n";
            }
            else {
                QJsonValue objectList = data.object().value("data");
                if (!objectList.isArray()) {
                    qDebug() << "Received malformed data\n";
                }
                else {
                    QJsonArray dataArray = objectList.toArray();
                    foreach(const QJsonValue &value, dataArray) {
                        if (value.isObject()) {
                            if (value.toObject().contains("id") && value.toObject().contains("title") && value.toObject().contains("artist") && value.toObject().contains("description")) {
                                QString label = QString::number(value.toObject().value("id").toInt()) + " : " + value.toObject().value("title").toString() + " by " + value.toObject().value("artist").toString();
                                mainSelector->addItem(label, QList<QVariant>() << value.toObject().value("id").toInt() << value.toObject().value("description").toString());
                            }
                            else {
                                qDebug() << "Received malformed data\n";
                            }
                        }
                    }
                }
            }
        } else {
            qDebug() << "Error:" << reply->errorString() << "\n";
            QMessageBox::critical(this,"Failed to connect!","Connection failed, check your network connection and restart the application or contact server owner, it may be down!");
        }
        reply->deleteLater();
    });
    connect(nm2, &QNetworkAccessManager::finished, this, [=](QNetworkReply *reply) {
        if (!(reply->error() == QNetworkReply::ConnectionRefusedError)) {
            QByteArray responseData = reply->readAll();
            selectedImage->loadFromData(responseData,"jpg");
            // qDebug() << responseData;
            if (selectedImage->isNull()) {
                qDebug() << "Image failed to load\n";
                QMessageBox::warning(this,"Image failed to load!","Image failed to load, contact server owner, it may be down!");
                selectedImage->load("fnf.jpg","jpg");
            }
            QGraphicsPixmapItem* newitem = new QGraphicsPixmapItem(*selectedImage);
            scene->clear();
            scene->setSceneRect(selectedImage->rect());
            scene->addItem(newitem);
        } else {
            qDebug() << "Error:" << reply->errorString() << "\n";
            QMessageBox::warning(this,"Failed to connect!","Failed to acquire image file from the server!");
        }
        reply->deleteLater();
    });

    QUrl url("http://localhost:8000/api/server?mode=1");
    QNetworkRequest request(url);
    nm1->get(request);

    desc = new QTextEdit(this);
    desc->setLineWrapMode(QTextEdit::WidgetWidth);
    desc->setReadOnly(true);
    desc->setMaximumWidth(400);
    desc->setPlaceholderText("Select something first");

    connect(mainSelector,&QComboBox::currentTextChanged,this,&SelectWindow::loadImage);

    mainLayout->setColumnMinimumWidth(1,400);
    mainLayout->addWidget(mainSelector,0,0);
    mainLayout->addWidget(mainView,0,1,3,3);
    mainLayout->addWidget(desc,2,0,1,1);
    setWindowTitle("MuseumViewer");
    resize(1600, 900);
    move(100,100);
    this->setLayout(mainLayout);
}

SelectWindow::~SelectWindow()
{

}

void SelectWindow::loadImage(QString text) {
    QList<QVariant> data = mainSelector->itemData(mainSelector->currentIndex()).value<QList<QVariant>>();
    qDebug() << QString::number(data.first().toInt());
    QUrl url("http://localhost:8000/api/server?mode=0&id="+QString::number(data.first().toInt()));
    QNetworkRequest request(url);
    nm2->get(request);
    QString description = data.last().toString();
    if (description.isEmpty()) {
        desc->setText("This item has no description");
    }
    else {
        desc->setText(data.last().toString());
    }
    return;
}
