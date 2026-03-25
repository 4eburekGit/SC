#include "selectwindow.h"

#include <QApplication>

#define MVIEVER_UI_TESTING

int main(int argc, char *argv[])
{
    QApplication a(argc, argv);
    SelectWindow* w = new SelectWindow();
    w->show();
    return a.exec();
}
