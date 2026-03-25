#include <QObject>
#include <QtTest/QTest>
#include <QTest>
#include <QTimer>
#include <QListView>
#include "selectwindow.h"

class TestSelectWindow : public QObject
{
    Q_OBJECT
public:
    explicit TestSelectWindow(QObject *parent = nullptr);
private slots:
    void integrate();

signals:
};

TestSelectWindow::TestSelectWindow(QObject *parent)
    : QObject{parent}
{}

void TestSelectWindow::integrate() {
    SelectWindow* w = new SelectWindow();
    w->show();
    QTest::qWaitForWindowExposed(w);
    QTest::mouseClick(w,Qt::LeftButton);
    QTest::qWait(1000);
    QComboBox* box = w->findChild<QComboBox*>();
    QVERIFY(box);
    qDebug() << "Found combobox\n";
    QTextEdit* desc = w->findChild<QTextEdit*>();
    QVERIFY(desc);
    qDebug() << "Found text\n";
    QTest::mouseClick(box,Qt::LeftButton);
    QTest::qWait(1000);
    QListView* dropList = box->findChild<QListView*>();
    QVERIFY(dropList);
    qDebug() << "Found droplist\n";
    QTest::qWaitForWindowExposed(dropList);
    int target = 3; // 4th element
    QModelIndex found = dropList->model()->index(target, 0);
    QRect foundItem = dropList->visualRect(found);
    QPoint itemCenter = foundItem.center();
    QTest::mouseClick(dropList->viewport(), Qt::LeftButton, Qt::NoModifier, itemCenter);
    qDebug() << "Selected Item\n";
    QTest::qWait(1000);
    QCOMPARE(box->currentIndex(),target);
    QCOMPARE(box->currentText(),"4758 : Stoke-by-Nayland by John Constable");
    QCOMPARE(desc->toPlainText(),QString("“What say you to a summer morning?” John Constable wrote of this painting in a letter to a friend. Even after many years living in London, Constable continued to portray the countryside, dear to him from boyhood. Stoke-by-Nayland lies a few miles from his native village of East Bergholt in Suffolk. In this view, he divided the canvas between a brilliant, airy vista toward the hamlet on the left and a shady, tunnel-like country lane leading off to the right. Constable explained in his letter that the painting depicted a speciﬁc time, a morning in “July or August, at eight or nine o’clock, after a slight shower during the night, to enhance the dews in the shadowed part of the picture.” The artist emphasized the abundance of water through his painting technique, ﬂecking the surface with white highlights to create an effect of sparkling wetness. Here, the whole scene appears dewy, with a stream and puddles in the foreground and a central tree that droops from the weight of rainwater, emphasizing the fertile land.Painted as much with a palette knife as with brushes, Stoke-by-Nayland lacks the ﬁnish of pictures Constable exhibited publicly; it was meant as a full-scale sketch for a work that he never realized. Nonetheless, this canvas seems to capture Constable’s delight in freely scribbling and scraping the image into existence—what it lacks in detail it gains in atmosphere. The roughness of the surface evokes the textures of real landscape, and the coexistence of natural and built elements in the scene embodies an ideal of harmony indicative of Constable’s vision of rural England."));
    qDebug() << "Passed normal image test\n";

    QTest::mouseClick(box,Qt::LeftButton);
    QTest::qWait(1000);
    dropList = box->findChild<QListView*>();
    QVERIFY(dropList);
    qDebug() << "Found droplist\n";
    QTest::qWaitForWindowExposed(dropList);
    target = 2; // 3rd element - no image
    QModelIndex found2 = dropList->model()->index(target, 0);
    QRect foundItem2 = dropList->visualRect(found2);
    QPoint itemCenter2 = foundItem2.center();
    QTimer::singleShot(1000,[&]{
        QWidgetList allWindows = QApplication::topLevelWidgets();
        foreach (QWidget *wid, allWindows) {
            if (wid->inherits("QMessageBox")) {
                QMessageBox *mb = qobject_cast<QMessageBox *>(wid);
                QTest::keyClick(mb, Qt::Key_Enter);
            }
        }
        qDebug() << "Closed warn window\n";
    });
    QTest::mouseClick(dropList->viewport(), Qt::LeftButton, Qt::NoModifier, itemCenter2);
    qDebug() << "Found missing image\n";

    QCOMPARE(box->currentIndex(),target);
    QCOMPARE(box->currentText(),"161 : Skyphos (Drinking Cup) by Ancient Greek");
    QCOMPARE(desc->toPlainText(),QString("During the course of the 5th and 4th centuries BCE, black vessels (commonly called black-glaze vessels) were made with increasing frequency in both Greece and South Italy. Many of them replicate the shape of metal vessels. Others have detailing that is molded or incised. While the quality of these vessels varies greatly, all would have been less expensive than vessels decorated in other contemporary techniques, for example, in red-figure."));
    qDebug() << "Passed\n";
    QTest::qWait(2000);
    w->close();
}
#include "tst_testscclient.moc"
QTEST_MAIN(TestSelectWindow);
