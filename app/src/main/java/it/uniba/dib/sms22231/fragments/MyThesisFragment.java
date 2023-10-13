package it.uniba.dib.sms22231.fragments;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import it.uniba.dib.sms22231.R;
import it.uniba.dib.sms22231.adapters.CustomListAdapter;
import it.uniba.dib.sms22231.config.RequirementTypes;
import it.uniba.dib.sms22231.model.Application;
import it.uniba.dib.sms22231.model.Attachment;
import it.uniba.dib.sms22231.model.CustomListData;
import it.uniba.dib.sms22231.model.Requirement;
import it.uniba.dib.sms22231.service.ApplicationService;
import it.uniba.dib.sms22231.service.AttachmentService;
import it.uniba.dib.sms22231.service.RequirementService;
import it.uniba.dib.sms22231.service.ThesisService;


public class MyThesisFragment extends Fragment {
    private final ApplicationService applicationService = ApplicationService.getInstance();
    private final ThesisService thesisService = ThesisService.getInstance();
    private final RequirementService requirementService = RequirementService.getInstance();
    private final AttachmentService attachmentService = AttachmentService.getInstance();
    private Application application;
    private String applicationId;
    private View view;
    private int caller;
    private List<Requirement> teacherRequirements;
    private List<Requirement> studentRequirements;
    private List<Attachment> attachments;
    private ArrayList<String> fileNames;
    private TextView noItemText;
    private TextView titleText;
    private TextView ownerText;
    private TextView descriptionText;
    private TextView requirementText;
    private TextView noRequirements;
    private TextView noFile;
    private TextView attachmentText;
    private ListView requirementsList;
    private ListView attachmentsList;

    public MyThesisFragment(String applicationId, int caller) {
        super();
        this.applicationId = applicationId;
        this.caller = caller;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_my_thesis, container, false);
        noItemText = view.findViewById(R.id.noThesisText);
        titleText = view.findViewById(R.id.titleText);
        ownerText = view.findViewById(R.id.ownerText);
        descriptionText = view.findViewById(R.id.descriptionText);
        requirementText = view.findViewById(R.id.textView6);
        noRequirements = view.findViewById(R.id.textNoReq);
        requirementsList = view.findViewById(R.id.reqList);
        attachmentText = view.findViewById(R.id.textView8);
        noFile = view.findViewById(R.id.textNoFile);
        attachmentsList = view.findViewById(R.id.fileList);


        fillFragment();

        return view;
    }

    private void fillFragment() {
        applicationService.getApplicationById(applicationId).subscribe(application -> {
            this.application = application;

            thesisService.getThesisById(application.thesisId, thesis -> {
                titleText.setText(thesis.title);
                String owner;
                if (caller == 4) {
                    owner = getString(R.string.student) + ": " + application.studentName;
                } else {
                    owner = getString(R.string.teacher) + ": " + thesis.teacherFullname;
                }
                ownerText.setText(owner);
                descriptionText.setText(thesis.description);

                if (caller == 4) {
                    requirementText.setVisibility(View.VISIBLE);
                    studentRequirements = application.requirements;
                    requirementService.getRequirementsByThesis((thesis)).subscribe(requirements -> {
                        teacherRequirements = requirements;

                        ArrayList<CustomListData> listDataArrayCustomList = new ArrayList<>();
                        boolean averageControl = false;
                        int teacherAverage = 0;
                        int studentAverage = 0;

                        for (Requirement r : teacherRequirements) {
                            if (r.description == RequirementTypes.average) {
                                teacherAverage = Integer.parseInt(r.value);
                                averageControl = true;
                            }
                        }
                        for (Requirement r : studentRequirements) {
                            if (r.description == RequirementTypes.average) {
                                studentAverage = Integer.parseInt(r.value);
                            }
                        }
                        if (averageControl) {
                            String averageText = getString(R.string.average) + ": " + studentAverage + "/" + teacherAverage;
                            CustomListData average = new CustomListData(studentAverage < teacherAverage ? R.drawable.clear : R.drawable.check, averageText);
                            listDataArrayCustomList.add(average);
                        }

                        boolean givenExam;

                        for (Requirement teacherReq : teacherRequirements) {
                            givenExam = false;
                            if (teacherReq.description == RequirementTypes.exam) {
                                for (Requirement studentReq : studentRequirements) {
                                    if (studentReq.description == RequirementTypes.exam) {
                                        if (teacherReq.id.equals(studentReq.id)) {
                                            givenExam = true;
                                        }
                                    }
                                }
                                String examText = getResources().getStringArray(R.array.requirements)[teacherReq.description.ordinal()] + ": " + teacherReq.value;
                                CustomListData exam = new CustomListData(givenExam ? R.drawable.check : R.drawable.clear, examText);
                                listDataArrayCustomList.add(exam);
                            }
                        }

                        if (listDataArrayCustomList.isEmpty()) {
                            requirementsList.setVisibility(View.GONE);
                            noRequirements.setVisibility(View.VISIBLE);
                        } else {
                            CustomListAdapter customListAdapter = new CustomListAdapter(getContext(), listDataArrayCustomList);
                            requirementsList.setAdapter(customListAdapter);
                            requirementsList.setVisibility(View.VISIBLE);
                            noRequirements.setVisibility(View.GONE);
                        }
                    });
                }
                if (caller != 4) {
                    attachmentText.setVisibility(View.VISIBLE);
                    attachmentService.getAttachmentsByThesis(thesis).subscribe(attachments -> {
                        this.attachments = attachments;
                        fileNames = new ArrayList<>();
                        for (Attachment attachment : attachments) {
                            fileNames.add(attachment.getFileName());
                        }
                        if (fileNames.isEmpty()){
                            noFile.setVisibility(View.VISIBLE);
                        } else {
                            noFile.setVisibility(View.GONE);
                            fillAttachmentsList();
                        }

                    });
                }
            });
        });
    }

    private void fillAttachmentsList() {
        ArrayList<CustomListData> attachmentsCustomListData = new ArrayList<>();
        attachments.forEach(attachment -> {
            CustomListData customListData = new CustomListData();
            customListData.setText(attachment.getFileName());
            switch (attachment.fileType) {
                case image:
                    customListData.setImageId(R.drawable.image);
                    break;
                case video:
                    customListData.setImageId(R.drawable.video);
                    break;
                case archive:
                    customListData.setImageId(R.drawable.zip);
                    break;
                case generic:
                    customListData.setImageId(R.drawable.file);
                    break;
                case document:
                    customListData.setImageId(R.drawable.pdf);
            }
            attachmentsCustomListData.add(customListData);
        });
        CustomListAdapter customListAdapter = new CustomListAdapter(getContext(), attachmentsCustomListData){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view1 = super.getView(position, convertView, parent);
                TextView textView = view1.findViewById(R.id.listText);
                textView.setTextColor(Color.BLUE);
                textView.setPaintFlags(textView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                return view1;
            }
        };

        attachmentsList.setAdapter(customListAdapter);
        attachmentsList.setVisibility(View.VISIBLE);
    }
}