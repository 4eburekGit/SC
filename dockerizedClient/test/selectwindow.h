#ifndef SELECTWINDOW_H
#define SELECTWINDOW_H

// interface
#include <QMainWindow>
#include <QGridLayout>
#include <QComboBox>
#include <QGraphicsView>
#include <QGraphicsScene>
#include <QGraphicsPixmapItem>
#include <QPixmap>
#include <QTextEdit>
#include <QList>
#include <QVariant>
#include <QWidget>
#include <QMessageBox>
// network
#include <QtNetwork/QNetworkAccessManager>
#include <QtNetwork/QNetworkRequest>
#include <QtNetwork/QNetworkReply>
#include <QJsonParseError>
#include <QJsonDocument>
#include <QJsonObject>
#include <QJsonArray>
#include <QJsonValue>
// logging
#include <QDebug>

class SelectWindow : public QWidget
{
    Q_OBJECT

public:
    SelectWindow(QWidget *parent = nullptr);
    ~SelectWindow();
private slots:
    void loadImage(QString);
private:
    QGridLayout* mainLayout;
    QComboBox* mainSelector;
    QTextEdit* desc;
    QPixmap* selectedImage;
    QGraphicsView* mainView;
    //
    QNetworkAccessManager* nm1; // for requesting tables
    QNetworkAccessManager* nm2; // for requesting images
};

#endif // SELECTWINDOW_H
