package com.idctdo.android;




import android.app.Activity;
import android.location.Location;

public class EQForm extends Activity {
		
		/*Defines the Shared Preferences memory spaces for the entire application in the parent Activity*/

		public static final String APP_SETTINGS = "mAppSettings";
		
		/**Spinners each change two settings; a "SPIN" and "STRING"
		*  _SPIN holds the selected position in the array as a numeric value
		*  _STRING holds the @string/___ value for the selected position
		*  The _SPIN memory space allows the user's input to be passed to an external file for use off App
		*/
				
		/**The following are EditText values
		 * 
		 */
		
		
		
		
		public static final String APP_SETTINGS_OTHER_SOIL         = "IfOther";
		public static final String APP_SETTINGS_OTHER_FAILURE	   = "IfOther";
		public static final String APP_SETTINGS_OTHER_OCCUPANCY	   = "IfOther";
		public static final String APP_SETTINGS_OTHER_SYSTEM	   = "IfOther";
		public static final String APP_SETTINGS_EARTHQUAKE		   = "Earthquake";
		public static final String APP_SETTINGS_SURVEYOR    	   = "Surveyor";
		public static final String APP_SETTINGS_DATE  		       = "Date";
		public static final String APP_SETTINGS_TIME			   = "Time";
		public static final String APP_SETTINGS_SURVEY_NAME		   = "SurveyName";
		public static final String APP_SETTINGS_LATIT      		   = "Latitude";
		public static final String APP_SETTINGS_LONGIT	           = "Longitude";
		public static final String APP_SETTINGS_ADDRESS  		   = "Address";
		public static final String APP_SETTINGS_STOREYS_A 		   = "StoreysAbove";
		public static final String APP_SETTINGS_STOREYS_B  		   = "StoreysBelow";
		public static final String APP_SETTINGS_SEC_HAZ            = "SecondaryHazard";
		public static final String APP_SETTINGS_DAMAGE_PIC    	   = "DamagePic";
		public static final String APP_SETTINGS_PIC_NAME      	   = "PicName";
		public static final String APP_SETTINGS_FILE_NAME          = "FileName";
		public static final String APP_SETTINGS_GENERAL			   = "General";
		
		/**The following are Spinner values 
		 * They are paired as "@id/generic_spinner_name" pairs in _SPIN _STRING
		 */
		
		public static final String APP_SETTINGS_OCCUPANCY_SPIN	   = "OccupancyTypeSpinner";
		public static final String APP_SETTINGS_OCCUP_STRING	   = "OccupancyTypeString";
		
		public static final String APP_SETTINGS_SOIL_SPIN   	   = "SoilTypeSpinner";
		public static final String APP_SETTINGS_SOIL_STRING  	   = "SoilTypeString";
		
		public static final String APP_SETTINGS_ORIG_ASS_SPIN 	   = "OriginalAssessmentSpinner";
		public static final String APP_SETTINGS_ORIG_ASS_STRING	   = "OriginalAssessmentString";
		
		public static final String APP_SETTINGS_NEW_ASS_SPIN 	   = "NewAssessmentSpinner";
		public static final String APP_SETTINGS_NEW_ASS_STRING 	   = "NewAssessmentString";
				
		public static final String APP_SETTINGS_SLOPING_SPIN  	   = "SlopingGroundSpin";
		public static final String APP_SETTINGS_SLOPING_STRING	   = "SlopingGroundString";
		
		public static final String APP_SETTINGS_REG_P_SPIN     	   = "RegularityInPlanSpin";
		public static final String APP_SETTINGS_REG_P_STRING  	   = "RegularityInPlanString";
		
		public static final String APP_SETTINGS_REG_E_SPIN    	   = "RegularityInElevationSpin";
		public static final String APP_SETTINGS_REG_E_STRING  	   = "RegularityInElevationString";
		
		public static final String APP_SETTINGS_DAMAGE_SPIN   	   = "DamageLevelSpinner";
		public static final String APP_SETTINGS_DAMAGE_STRING      = "DamageLevelString";
		
		public static final String APP_SETTINGS_FAIL_MODE_SPIN     = "FailureModeSpin";
		public static final String APP_SETTINGS_FAIL_MODE_STRING   = "FailureModeString";
		
		public static final String APP_SETTINGS_STRUCT_DET_SPIN    = "StructuralDetailsSpin";
		public static final String APP_SETTINGS_STRUCT_DET_STRING  = "StructuralDetailstring";
		
		public static final String APP_SETTINGS_COLUMN_MAIN_SPIN   = "ColumnReoSpin";
		public static final String APP_SETTINGS_COLUMN_MAIN_STRING = "ColumnReoString";
		
		public static final String APP_SETTINGS_CM_NUMBER_SPIN     = "ColumnReoNumberSpin";
		public static final String APP_SETTINGS_CM_NUMBER_STRING   = "ColumnReoNumberString";
		
		public static final String APP_SETTINGS_COLUMN_SEC_SPIN    = "ColumnConSpin";
		public static final String APP_SETTINGS_COLUMN_SEC_STRING  = "ColumnConString";
		
		public static final String APP_SETTINGS_CS_SPACING_SPIN    = "ColumnSecSpacingSpin";
		public static final String APP_SETTINGS_CS_SPACING_STRING  = "ColumnSecSpacingString";
		
		public static final String APP_SETTINGS_BEAM_MAIN_SPIN 	   = "BeamMainSpin";
		public static final String APP_SETTINGS_BEAM_MAIN_STRING   = "BeamMainString";
		
		public static final String APP_SETTINGS_BM_NUMBER_SPIN     = "BeamMainNumberSpin";
		public static final String APP_SETTINGS_BM_NUMBER_STRING   = "BeamMainNumberString";
		
		public static final String APP_SETTINGS_BEAM_SHEAR_SPIN    = "BeamShearSpin";
		public static final String APP_SETTINGS_BEAM_SHEAR_STRING  = "BeamShearString";
		
		public static final String APP_SETTINGS_BS_SPACING_SPIN    = "BeamShearSpacingSpin";
		public static final String APP_SETTINGS_BS_SPACING_STRING  = "BeamShearSpacingString";
		
		

		
}

