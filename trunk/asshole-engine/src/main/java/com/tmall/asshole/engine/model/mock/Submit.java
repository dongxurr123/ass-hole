package com.tmall.asshole.engine.model.mock;

public class Submit {
	
	public boolean execute(Integer hsfResult){
        System.err.println("submit~~~~~~~~~~~~~~~");		
        System.out.println("��������̵�Ԥ�����Ƿ��㹻");
        System.out.println("���ù�Ƶ��ʽ�ӿڼ��");
        if(hsfResult==1){
        	//�����ʽ� ����
        	System.out.println("�ʽ�ۿ�ɹ�");
        	return true;
        }
    	System.out.println("�ʽ�ۿ�ʧ��");
        return false;
	}
}
